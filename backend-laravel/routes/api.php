<?php

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;
use App\Http\Controllers\Api\SignupController;
use App\Http\Controllers\Api\UserController;
use App\Http\Controllers\Api\LoginController;
use App\Http\Controllers\Api\TokenController;


/*
|--------------------------------------------------------------------------
| API Routes
|--------------------------------------------------------------------------
|
| Here is where you can register API routes for your application. These
| routes are loaded by the RouteServiceProvider and all of them will
| be assigned to the "api" middleware group. Make something great!
|
*/

Route::middleware('auth:sanctum')->get('/user', function (Request $request) {
    return $request->user();
});

Route::prefix('auth')->group(function () {
    Route::post('/signup', [SignupController::class, 'signup']);
    Route::post('/verify-email', [SignupController::class, 'verifyEmail']);
    Route::get('/verify-email-existence/{email}', [SignupController::class, 'verifyEmailExistence']);

    Route::post('/login', [LoginController::class, 'login']);

    Route::get('/reset-login-attempts', [LoginController::class, 'resetLoginAttempts']);
    Route::get('/reset-verification-attempts', [SignupController::class, 'resetVerificationAttempts']);

    Route::get('/user', [TokenController::class, 'user']);

    Route::post('/logout', [TokenController::class, 'logout'])
        ->middleware('token.auth');

    Route::get('/token/validate', [TokenController::class, 'checkTokenValidity'])
        ->middleware('token.auth');

});

Route::prefix('users')->group(function () {
    Route::put('/{id}', [UserController::class, 'update']);
});
