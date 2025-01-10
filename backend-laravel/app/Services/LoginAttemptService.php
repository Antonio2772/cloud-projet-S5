<?php

namespace App\Services;

use App\Models\User;
use Carbon\Carbon;

class LoginAttemptService
{
    protected $maxAttempts;
    protected $blockDurationMinutes;

    public function __construct()
    {
        $this->maxAttempts = config('auth.max_login_attempts', 3);
        $this->blockDurationMinutes = config('auth.block_duration_minutes', 15);
    }

    public function recordFailedAttempt(User $user)
    {
        $user->increment('login_attempts');
        $user->last_login_attempt_at = now();
        $user->save();
    }

    public function resetLoginAttempts(User $user)
    {
        $user->update([
            'login_attempts' => 0,
            'last_login_attempt_at' => null
        ]);
    }

    public function isBlocked(User $user)
    {
        // If attempts exceed max, check if block period has passed
        if ($user->login_attempts >= $this->maxAttempts) {
            $lastAttemptTime = $user->last_login_attempt_at;
            $blockUntil = $lastAttemptTime ? 
                Carbon::parse($lastAttemptTime)->addMinutes($this->blockDurationMinutes) : 
                null;

            return $blockUntil && now()->lessThan($blockUntil);
        }

        return false;
    }
}