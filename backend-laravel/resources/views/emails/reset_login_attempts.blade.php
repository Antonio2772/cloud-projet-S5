<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Reset Login Attempts</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            line-height: 1.6;
            margin: 20px;
            padding: 20px;
            background-color: #f9f9f9;
        }
        .container {
            background-color: #ffffff;
            border-radius: 5px;
            padding: 20px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
        }
        a {
            color: #007BFF;
            text-decoration: none;
        }
        a:hover {
            text-decoration: underline;
        }
        .footer {
            margin-top: 20px;
            font-size: 0.9em;
            color: #555555;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>Reset Login Attempts</h1>
        
        <p>Dear {{ $userName }},</p>

        <p>You have reached the maximum number of login attempts for your account. To regain access, please click the link below to reset your login attempts:</p>

        <p><a href="{{ $resetLink }}">Reset Login Attempts</a></p>
        <p>If you did not attempt to log in, please contact our support team.</p>
        <p>This link will expire in 1 hour.</p>

        <p class="footer">Best regards,<br>Your App Support Team</p>
    </div>
</body>
</html>