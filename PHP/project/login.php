<?php
session_start();
if (isset($_SESSION['user']) || isset($_SESSION['admin'])) {
    header('Location: user.php' . (isset($_SESSION['admin']) ? '?role=admin' : ''));
    exit;
}

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $username = $_POST['username'];
    $password = $_POST['password'];

    if ($username == 'admin' && $password == 'admin') {
        $_SESSION['admin'] = true;
        header('Location: admin.php');
    } elseif ($username == 'user' && $password == 'user') {
        $_SESSION['user'] = true;
        header('Location: user.php');
    } else {
        echo "<p>Invalid username or password.</p>";
    }
}

?>
<!DOCTYPE html>
<html lang="pl">
<head>
    <meta charset="UTF-8">
    <title>Login</title>
    <link rel="stylesheet" href="css/styles.css">
</head>
<body>
    <h1>Login</h1>
    <form method="post" action="">
        <label for="username">Username:</label>
        <input type="text" id="username" name="username" required><br><br>
        <label for="password">Password:</label>
        <input type="password" id="password" name="password" required><br><br>
        <button type="submit">Login</button>
    </form>
    <a href="contact.php">Contact with us!</a>
</body>
</html>
