<?php

namespace App\Console\Commands;

use Illuminate\Console\Command;
use App\Models\User;
use Carbon\Carbon;

class CleanupExpiredTokens extends Command
{
    protected $signature = 'tokens:cleanup';
    protected $description = 'Remove expired authentication tokens';

    public function handle()
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

        $this->info("Cleaned {$expiredTokens->count()} expired tokens.");
        
        return 0;
    }
}