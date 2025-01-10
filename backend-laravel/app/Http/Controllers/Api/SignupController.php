<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Services\VerificationAttemptService;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Hash;
use Illuminate\Support\Facades\Validator;
use Illuminate\Support\Facades\Mail;
use Illuminate\Support\Facades\Http; 
use Illuminate\Support\Str;
use App\Models\User;
use Carbon\Carbon;
use OpenApi\Annotations as OA;
class SignupController extends Controller
{   
    protected $verificationAttemptService;

    protected $maxVerificationAttempts;

    public function __construct(VerificationAttemptService $verificationAttemptService)
    {
        $this->verificationAttemptService = $verificationAttemptService;
        $this->maxVerificationAttempts = config('auth.max_verification_attempts', 3);
    }

    /** 
    * @OA\Post(
    *     path="/auth/signup",
    *     tags={"Authentication"},
    *     summary="Register a new user",
    *     @OA\RequestBody(
    *         required=true,
    *         @OA\JsonContent(
    *             required={"email","password","name","birthday"},
    *             @OA\Property(property="email", type="string", format="email"),
    *             @OA\Property(property="password", type="string", minimum=6),
    *             @OA\Property(property="name", type="string"),
    *             @OA\Property(property="birthday", type="string", format="date")
    *         )
    *     ),
    *     @OA\Response(
    *         response=201,
    *         description="User registered successfully",
    *         @OA\JsonContent(
    *             @OA\Property(property="status", type="string", example="success"),
    *             @OA\Property(property="message", type="string"),
    *             @OA\Property(property="user_id", type="integer")
    *         )
    *     ),
    *     @OA\Response(response=400, ref="#/components/schemas/Error"),
    *     @OA\Response(response=500, ref="#/components/schemas/Error")
    * )
    */
    public function signup(Request $request)
    {
        $validator = Validator::make($request->all(), [
            'email' => 'required|email|unique:users,user_email',
            'password' => 'required|min:6',
            'name' => 'required|string|max:255',
            'birthday' => 'required|date'
        ]);

        if ($validator->fails()) {
            return response()->json([
                'status' => 'error',
                'errors' => $validator->errors()
            ], 400);
        }

        // Check email existence
        $emailExistenceResponse = $this->verifyEmailExistence($request->email);
        $emailExistence = json_decode($emailExistenceResponse->getContent(), true); 

        if (!$emailExistence['status']) {
            return response()->json([
                'status' => 'error',
                'errors' => $emailExistence['message']
            ]);
        }

        // generate code pin
        $verificationCode = str_pad(rand(0, 9999), 4, '0', STR_PAD_LEFT);

        try {
            // Create user with unverified status
            $user = User::create([
                'user_name' => $request->name,
                'user_email' => $request->email,
                'user_password' => Hash::make($request->password),
                'user_birthday' => $request->birthday,
                'email_verification_code' => $verificationCode,
                'email_verified_at' => null,

                // add one hour to match mada hour
                // the pin expiration is 3 minutes 
                'verification_code_expires_at' => Carbon::now()->addMinutes(60)->addMinutes(3), 
            ]);

            $this->sendVerificationEmail($user->user_email, $verificationCode);

            return response()->json([
                'status' => 'success',
                'message' => 'User registered. Check your email for verification code.',
                'user_id' => $user->id
            ], 201);

        } catch (\Exception $e) {
            return response()->json([
                'status' => 'error',
                'message' => 'Registration failed: ' . $e->getMessage()
            ], 500);
        }
    }

    private function sendVerificationEmail($email, $verificationCode)
    {
        Mail::send('emails.verification', ['verificationCode' => $verificationCode], function ($message) use ($email) {
            $message->to($email)
                    ->subject('Verify Your Email');
        });
    }

    /** 
    * @OA\Post(
    *     path="/auth/verify-email",
    *     tags={"Authentication"},
    *     summary="Verify email with PIN code",
    *     @OA\RequestBody(
    *         required=true,
    *         @OA\JsonContent(
    *             required={"email","verification_code"},
    *             @OA\Property(property="email", type="string", format="email"),
    *             @OA\Property(property="verification_code", type="string", pattern="^\d{4}$")
    *         )
    *     ),
    *     @OA\Response(response=200, ref="#/components/schemas/Success"),
    *     @OA\Response(response=400, ref="#/components/schemas/Error"),
    *     @OA\Response(response=404, ref="#/components/schemas/Error")
    * )
    */
    public function verifyEmail(Request $request)
    {
        $validator = Validator::make($request->all(), [
            'email' => 'required|email',
            'verification_code' => 'required|string|size:4'
        ]);

        if ($validator->fails()) {
            return response()->json([
                'status' => 'error',
                'errors' => $validator->errors()
            ], 400);
        }

        $user = User::where('user_email', $request->email)->first();

        if (!$user) {
            return response()->json([
                'status' => 'error',
                'message' => 'User not found'
            ], 404);
        }

        // Check if code is expired
        if (Carbon::now()->addMinutes(60)->isAfter($user->verification_code_expires_at)) {
            $user->delete();
            return response()->json([
                'status' => 'error',
                'message' => 'Verification code has expired'
            ], 400);
        }

        // Check if verification code matches
        if ($user->email_verification_code !== $request->verification_code) {
            // Increment verification attempts
            $user->verification_attempts += 1;
            $user->last_verification_attempt_at = now()->addMinutes(60);
            $user->save();

            // Check if max attempts reached
            if ($user->verification_attempts >= $this->maxVerificationAttempts) {
                $this->sendResetVerificationAttemptsEmail($user);

                // Check if user is blocked due to too many attempts
                if ($this->verificationAttemptService->isBlocked($user)) {
                    return response()->json([
                        'status' => 'error',
                        'message' => 'Too many login attempts. Please reset your attempts.'
                    ], 403);
                }
            }

            return response()->json([
                'status' => 'error',
                'message' => 'Invalid verification code',
                'attempts_left' => $this->maxVerificationAttempts - $user->verification_attempts
            ], 400);
        }

        // Mark email as verified
        $user->email_verified_at = Carbon::now()->addMinutes(60);
        $user->email_verification_code = null;  
        $user->verification_code_expires_at = null;
        $user->verification_attempts = 0;
        $user->save();

        return response()->json([
            'status' => 'success',
            'message' => 'Email verified successfully'
        ], 200);
    }

    /** 
    * @OA\Get(
    *     path="/auth/verify-email-existence/{email}",
    *     tags={"Authentication"},
    *     summary="Check if email exists and is valid",
    *     @OA\Parameter(
    *         name="email",
    *         in="path",
    *         required=true,
    *         @OA\Schema(type="string", format="email")
    *     ),
    *     @OA\Response(
    *         response=200,
    *         description="Email validation result",
    *         @OA\JsonContent(
    *             @OA\Property(property="status", type="boolean"),
    *             @OA\Property(property="message", type="string"),
    *             @OA\Property(property="data", type="object")
    *         )
    *     ),
    *     @OA\Response(response=400, ref="#/components/schemas/Error"),
    *     @OA\Response(response=500, ref="#/components/schemas/Error")
    * )
    */  
    public function verifyEmailExistence($email){
        $apiKey = env('ABSTRACT_API_KEY');
        $url = "https://emailvalidation.abstractapi.com/v1/?api_key={$apiKey}&email=" . urlencode($email);

        try {
            $response = Http::get($url);
            $data = $response->json();

            // Analyse détaillée de la réponse
            $result = [
                'is_valid' => $data['deliverability'] === 'DELIVERABLE',
                'email' => $data['email'],
                'quality_score' => $data['quality_score'],
                'details' => [
                    'is_valid_format' => $data['is_valid_format']['value'],
                    'is_free_email' => $data['is_free_email']['value'],
                    'is_disposable_email' => $data['is_disposable_email']['value'],
                    'is_role_email' => $data['is_role_email']['value'],
                    'is_catchall_email' => $data['is_catchall_email']['value'],
                    'is_mx_found' => $data['is_mx_found']['value'],
                    'is_smtp_valid' => $data['is_smtp_valid']['value']
                ]
            ];

            // Conditions de validité plus strictes
            $isValidEmail = $result['is_valid'] &&
                            $result['details']['is_valid_format'] &&
                            $result['details']['is_mx_found'] &&
                            $result['details']['is_smtp_valid'] &&
                            !$result['details']['is_disposable_email'];

            // Réponse basée sur la validité
            if ($isValidEmail) {
                return response()->json([
                    'status' => true,
                    'message' => 'Email valide et existant',
                    'data' => $result
                ]);
            } else {
                return response()->json([
                    'status' => false,
                    'message' => 'Email invalide ou non existant',
                    'data' => $result
                ], 400);
            }

        } catch (\Exception $e) {
            return response()->json([
                'status' => false,
                'message' => 'Erreur de vérification : ' . $e->getMessage()
            ], 500);
        }
    }

    /** 
    * @OA\Get(
    *     path="/auth/reset-verification-attempts",
    *     tags={"Authentication"},
    *     summary="Reset verification attempts",
    *     @OA\Parameter(
    *         name="email",
    *         in="query",
    *         required=true,
    *         @OA\Schema(type="string", format="email")
    *     ),
    *     @OA\Parameter(
    *         name="reset_token",
    *         in="query",
    *         required=true,
    *         @OA\Schema(type="string")
    *     ),
    *     @OA\Response(response=200, ref="#/components/schemas/Success"),
    *     @OA\Response(response=400, ref="#/components/schemas/Error")
    * )
    */ 
    public function resetVerificationAttempts(Request $request)
    {
        $validator = Validator::make($request->all(), [
            'email' => 'required|email',
            'reset_token' => 'required|string'
        ]);

        if ($validator->fails()) {
            return response()->json([
                'status' => 'error',
                'errors' => $validator->errors()
            ], 400);
        }

        $user = User::where('user_email', $request->input('email'))
            ->where('reset_verification_attempts_token', $request->input('reset_token'))
            ->first();

        if (!$user || !$user->reset_verification_attempts_token_expires_at || 
            now()->isAfter($user->reset_verification_attempts_token_expires_at)) {
            return response()->json([
                'status' => 'error',
                'message' => 'Invalid or expired reset token'
            ], 400);
        }

        // Reset verification attempts
        $user->verification_attempts = 0;
        $user->reset_verification_attempts_token = null;
        $user->reset_verification_attempts_token_expires_at = null;
        $user->save();

        return response()->json([
            'status' => 'success',
            'message' => 'Verification attempts reset successfully'
        ]);
    }

    private function sendResetVerificationAttemptsEmail($user)
    {
        $resetToken = Str::random(40);
        $resetTokenExpiration = now()->addHours(1);

        $user->update([
            'reset_verification_attempts_token' => $resetToken,
            'reset_verification_attempts_token_expires_at' => $resetTokenExpiration
        ]);

        $resetLink = "http://127.0.0.1:8000/api/auth/reset-verification-attempts?email={$user->user_email}&reset_token={$resetToken}";

        Mail::send('emails.reset_verification_attempts', [
            'resetLink' => $resetLink,
            'userName' => $user->user_name
        ], function ($message) use ($user) {
            $message->to($user->user_email)
                    ->subject('Reset Verification Attempts');
        });
    }
}