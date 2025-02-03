<?php
session_start();
if (!isset($_SESSION['user'])) {
    header('Location: login.php');
    exit;
}

include 'db_config.php';
include 'functions.php';

$languages = getLanguage(isset($_GET['lang']) ? $_GET['lang'] : 'pl');

$contents = getContent("user");
?>
<!DOCTYPE html>
<html lang="<?php echo isset($_GET['lang']) ? $_GET['lang'] : 'pl'; ?>">
<head>
    <meta charset="UTF-8">
    <title>User Panel</title>
    <link rel="stylesheet" href="css/styles.css">
</head>
<body>
    <h1>Welcome, User!</h1>
    <select onchange="location.href='user.php?lang='+this.value;">
        <option value="pl" <?php if (isset($_GET['lang']) && $_GET['lang'] == 'pl') echo 'selected'; ?>>PL</option>
        <option value="en" <?php if (isset($_GET['lang']) && $_GET['lang'] == 'en') echo 'selected'; ?>>EN</option>
    </select>
    <h2><?php echo $languages['contents']; ?></h2>
    <ul>
        <?php foreach ($contents as $content): ?>
            <li><?php echo htmlspecialchars($content['title']); ?> - <?php echo html_entity_decode($content['content']); ?></li>
        <?php endforeach; ?>
    </ul>
    <a href="logout.php">Logout</a>
</body>
</html>
