<?php

use Illuminate\Support\Facades\Route;
use App\Http\Controllers\Api\SignupController;

/*
|--------------------------------------------------------------------------
| Web Routes
|--------------------------------------------------------------------------
|
| Here is where you can register web routes for your application. These
| routes are loaded by the RouteServiceProvider and all of them will
| be assigned to the "web" middleware group. Make something great!
|
*/

Route::get('/', function () {
    return view('welcome');
});

Route::get('/auth/google/callback', [SignupController::class, 'handleCallback'])->name('google.callback'); 
Route::get('/auth/google', [SignupController::class, 'authenticate'])->name('google.auth');