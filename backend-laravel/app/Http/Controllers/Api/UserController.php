<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use App\Models\User;
use Illuminate\Support\Facades\Validator;
use Illuminate\Support\Facades\Hash;
use OpenApi\Annotations as OA;

class UserController extends Controller
{
    /** 
    * @OA\Put(
    *     path="/users/{id}",
    *     tags={"Users"},
    *     summary="Update user information",
    *     @OA\Parameter(
    *         name="id",
    *         in="path",
    *         required=true,
    *         @OA\Schema(type="integer")
    *     ),
    *     @OA\RequestBody(
    *         required=true,
    *         @OA\JsonContent(
    *             @OA\Property(property="user_name", type="string", maxLength=255),
    *             @OA\Property(property="user_password", type="string", minLength=8),
    *             @OA\Property(property="user_birthday", type="string", format="date")
    *         )
    *     ),
    *     @OA\Response(
    *         response=200,
    *         description="User updated successfully",
    *         @OA\JsonContent(
    *             @OA\Property(property="status", type="string", example="success"),
    *             @OA\Property(property="message", type="string"),
    *             @OA\Property(property="data", type="object")
    *         )
    *     ),
    *     @OA\Response(response=422, ref="#/components/schemas/Error"),
    *     @OA\Response(response=404, ref="#/components/schemas/Error")
    * )
    */
    public function update(Request $request, $id)
    {
        $validator = Validator::make($request->all(), [
            'user_name' => 'sometimes|string|max:255',
            'user_password' => 'sometimes|string|min:8',
            'user_birthday' => 'sometimes|date',
        ]);

        if ($validator->fails()) {
            return response()->json([
                'status' => 'error',
                'errors' => $validator->errors(),
            ], 422);
        }

        $user = User::find($id);

        if (!$user) {
            return response()->json([
                'status' => 'error',
                'message' => 'User not found.',
            ], 404);    
        }

        $data = $request->except(['user_password']);
        $user->fill($data);

        if ($request->has('user_password')) {
            $user->user_password = Hash::make($request->user_password);
        }

        $user->save();

        return response()->json([
            'status' => 'success',
            'message' => 'User updated successfully.',
            'data' => $user,
        ], 200);
    }
}