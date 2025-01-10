<?php

namespace App\Services;

use App\Models\User;
use Carbon\Carbon;

class VerificationAttemptService
{
    protected $maxAttempts;
    protected $blockDurationMinutes;

    public function __construct()
    {
        $this->maxAttempts = config('auth.max_verification_attempts', 3);
        $this->blockDurationMinutes = config('auth.block_duration_minutes', 15);
    }

    public function recordFailedAttempt(User $user)
    {
        $user->increment('verification_attempts');
        $user->last_verification_attempt_at = now();
        $user->save();
    }

    public function resetverificationAttempts(User $user)
    {
        $user->update([
            'verification_attempts' => 0,
            'last_verification_attempt_at' => null
        ]);
    }

    public function isBlocked(User $user)
    {
        // If attempts exceed max, check if block period has passed
        if ($user->verification_attempts >= $this->maxAttempts) {
            $lastAttemptTime = $user->last_verification_attempt_at;
            $blockUntil = $lastAttemptTime ? 
                Carbon::parse($lastAttemptTime)->addMinutes($this->blockDurationMinutes) : 
                null;

            return $blockUntil && now()->lessThan($blockUntil);
        }

        return false;
    }
}