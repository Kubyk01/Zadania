<?php
session_start();
if (!isset($_SESSION['admin']) || $_SESSION['admin'] !== true) {
    header('Location: login.php');
    exit;
}

include 'db_config.php';
include 'functions.php';

$languages = getLanguage('en');

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $title = $_POST['title'];
    $content = $_POST['content'];
    $access = isset($_POST['useracces']) ? 1 : 0;
    
    addContent($title, $content, $access);
}


$contents = getContent("admin");
?>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Admin Panel</title>
    <link rel="stylesheet" href="css/styles.css">
    <script src="https://cdn.ckeditor.com/4.20.1/standard/ckeditor.js"></script>
</head>
<body>
    <h1>Welcome, Admin!</h1>
    <form method="post" action="">
        <label for="title"><?php echo $languages['title']; ?></label>
        <input type="text" id="title" name="title" required>
        <br></br>
        <p>Access for users<input type="checkbox" id="useracces" name="useracces"></p>
        <br></br>
        <label for="content"><?php echo $languages['content']; ?></label>
        <textarea id="content" name="content" rows="10" cols="50"></textarea>
        <br></br>
        <button type="submit"><?php echo $languages['save']; ?></button>
    </form>

    <h2>Contents</h2>
    <ul>
        <?php foreach ($contents as $content): ?>
            <li><?php echo htmlspecialchars($content['title']); ?> - <?php echo html_entity_decode($content['content']); ?></li>
        <?php endforeach; ?>
    </ul>

    <a href="logout.php">Logout</a>

    <script>
        CKEDITOR.replace('content', {
            toolbar: [
                { name: 'document', items: ['Source', '-', 'NewPage', 'Preview'] },
                { name: 'clipboard', items: ['Undo', 'Redo'] },
                { name: 'editing', items: ['Find', 'Replace', '-', 'SelectAll'] },
                '/',
                { name: 'basicstyles', items: ['Bold', 'Italic', 'Underline', 'Strike', '-', 'RemoveFormat'] },
                { name: 'paragraph', items: ['NumberedList', 'BulletedList', '-', 'Outdent', 'Indent'] },
                { name: 'insert', items: ['Image', 'Table', 'HorizontalRule', 'SpecialChar'] },
                { name: 'styles', items: ['Format', 'Font', 'FontSize'] }
            ],
            height: 300
        });
    </script>
</body>
</html>