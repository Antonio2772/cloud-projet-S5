<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class User extends Model
{
    use HasFactory;

    public $timestamps = false;

    protected $table = 'users';

    protected $fillable = [
        'user_name',
        'user_email',
        'user_password',
        'user_birthday',
        'token_last_used_at',
        'token_expires_at',
        'token',
        'email_verification_code',
        'verification_code_expires_at',
        'email_verified_at',
        'reset_attempts_token',
        'reset_attempts_token_expires_at',
        'login_attempts',
        'last_login_attempt_at',
        'verification_attempts',
        'last_verification_attempt_at',
        'reset_verification_attempts_token',
        'reset_verification_attempts_token_expires_at'
    ];

    protected $hidden = [
        'user_password',
    ];

    protected $casts = [
        'token_last_used_at' => 'datetime',
        'token_expires_at' => 'datetime',
        'user_birthday' => 'date',
        'email_verified_at' => 'datetime',
        'verification_code_expires_at' => 'datetime'
    ];
}