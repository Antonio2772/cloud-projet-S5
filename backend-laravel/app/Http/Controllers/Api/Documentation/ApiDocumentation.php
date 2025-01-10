<?php

namespace App\Http\Controllers\Api\Documentation;

/**
 * @OA\Info(
 *     title="Laravel User Authentication API",
 *     version="1.0.0",
 *     description="API endpoints for user authentication, registration, and management"
 * )
 * 
 * @OA\Server(
 *     url="http://localhost:8000/api",
 *     description="Local development server"
 * )
 * 
 * @OA\SecurityScheme(
 *     type="http",
 *     scheme="bearer",
 *     securityScheme="BearerAuth"
 * )
 */
class ApiDocumentation
{
    
}