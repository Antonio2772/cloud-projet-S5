<?php

namespace App\Helpers;

class ResponseHelper
{

    /**
     * Standardized success response.
     *
     * @param mixed $data
     * @param array|null $meta
     * @return array
     */
    public static function success($data, $meta = null): array
    {
        return [
            'status' => 'success',
            'data' => $data,
            'meta' => $meta,
            'error' => null,
        ];
    }

    /**
     * Standardized error response.
     *
     * @param int $code
     * @param string $message
     * @return array
     */
    public static function error(int $code, string $message, array $details = null): array
    {
        return [
            'status' => 'error',
            'data' => null,
            'meta' => null,
            'error' => [
                'code' => $code,
                'message' => $message,
                'details' => $details
            ],
        ];
    }
}