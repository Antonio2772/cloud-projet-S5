<?php

namespace App\Http\Middleware;

use Closure;
use Illuminate\Http\Request;
use App\Models\User;
use Carbon\Carbon;

class TokenAuthentication
{
    public function handle(Request $request, Closure $next)
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

        // Check token expiration
        if (!$user->token_expires_at || Carbon::now()->isAfter($user->token_expires_at)) {
            // Clear expired token
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

        // Optionally update last used time
        $user->update([
            'token_last_used_at' => Carbon::now()
        ]);

        // Attach user to the request for use in controllers
        $request->merge(['authenticated_user' => $user]);

        return $next($request);
    }
}