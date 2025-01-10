<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use App\Models\User;
use Carbon\Carbon;
use OpenApi\Annotations as OA;

class TokenController extends Controller
{
    /** 
    * @OA\Post(
    *     path="/auth/logout",
    *     tags={"Authentication"},
    *     summary="Logout user",
    *     security={{"BearerAuth":{}}},
    *     @OA\Response(response=200, ref="#/components/schemas/Success"),
    *     @OA\Response(response=401, ref="#/components/schemas/Error")
    * )
    */ 
    public function logout(Request $request)
    {
        // retrieve the user token
        $token = $request->bearerToken();

        if (!$token) {
            return response()->json([
                'status' => 'error',
                'message' => 'No token provided'
            ], 401);
        }

        $user = User::where('token', $token)->first();

        if (!$user) {
            return response()->json([
                'status' => 'error',
                'message' => 'Invalid token'
            ], 401);
        }

        // Clear the token
        $user->update([
            'token' => null,
            'token_expires_at' => null,
            'token_last_used_at' => null
        ]);

        return response()->json([
            'status' => 'success',
            'message' => 'Logged out successfully'
        ]);
    }

    /** 
    * @OA\Get(
    *     path="/auth/token/validate",
    *     tags={"Authentication"},
    *     summary="Validate authentication token",
    *     security={{"BearerAuth":{}}},
    *     @OA\Response(
    *         response=200,
    *         description="Token is valid",
    *         @OA\JsonContent(
    *             @OA\Property(property="status", type="string", example="success"),
    *             @OA\Property(property="message", type="string"),
    *             @OA\Property(property="expires_at", type="string", format="date-time")
    *         )
    *     ),
    *     @OA\Response(response=401, ref="#/components/schemas/Error")
    * )
    */
    public function checkTokenValidity(Request $request)
    {
        $token = $request->bearerToken();

        if (!$token) {
            return response()->json([
                'status' => 'error',
                'message' => 'No token provided'
            ], 401);
        }

        $user = User::where('token', $token)->first();

        if (!$user) {
            return response()->json([
                'status' => 'error',
                'message' => 'Invalid token'
            ], 401);
        }

        // Check if token is expired
        if (!$user->token_expires_at || Carbon::now()->isAfter($user->token_expires_at)) {
            // Token is expired, clear it
            $user->update([
                'token' => null,
                'token_expires_at' => null,
                'token_last_used_at' => null
            ]);

            return response()->json([
                'status' => 'error',
                'message' => 'Token has expired'
            ], 401);
        }

        // Update last used time to extend potential session tracking
        $user->update([
            'token_last_used_at' => Carbon::now()
        ]);

        return response()->json([
            'status' => 'success',
            'message' => 'Token is valid',
            'expires_at' => $user->token_expires_at
        ]);
    }


    public function user(Request $request)
    {
        $token = $request->query('token');

        if (!$token) {
            return response()->json([
                'status' => 'error',
                'message' => 'No token provided'
            ], 401);
        }

        $user = User::where('token', $token)->first();

        if (!$user) {
            return response()->json([
                'status' => 'error',
                'message' => 'Invalid token'
            ], 401);
        }

        // Check if token is expired
        if (!$user->token_expires_at || Carbon::now()->isAfter($user->token_expires_at)) {
            $user->update([
                'token' => null,
                'token_expires_at' => null,
                'token_last_used_at' => null
            ]);

            return response()->json([
                'status' => 'error',
                'message' => 'Token has expired'
            ], 401);
        }

        // Update last used time
        $user->update([
            'token_last_used_at' => Carbon::now()
        ]);

        return response()->json([
            'status' => 'success',
            'user' => [
                'id' => $user->id,
                'name' => $user->user_name,
                'email' => $user->user_email,
                'role' => $user->role,
                'token_expires_at' => $user->token_expires_at
            ]
        ]);
    }

    public function cleanupExpiredTokens()
    {
        $expiredTokens = User::whereNotNull('token')
            ->where('token_expires_at', '<', Carbon::now())
            ->get();

        foreach ($expiredTokens as $user) {
            $user->update([
                'token' => null,
                'token_expires_at' => null,
                'token_last_used_at' => null
            ]);
        }

        return response()->json([
            'status' => 'success',
            'cleaned_tokens' => $expiredTokens->count()
        ]);
    }
}