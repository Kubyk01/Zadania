<?php
function getLanguage($lang = 'pl') {
    $languages = json_decode(file_get_contents('languages/' . $lang . '.json'), true);
    return $languages;
}

function addContent($title, $content, $useraccess) {
    global $pdo;
    
    $stmt = $pdo->prepare("INSERT INTO contents (title, content, UserAccess) VALUES (:title, :content, :useraccess)");
    $stmt->execute([
        'title' => $title,
        'content' => $content,
        'useraccess' => $useraccess
    ]);
}

function getContent($userId) {
    global $pdo;
    if ($userId === "admin") {
        $stmt = $pdo->prepare("SELECT * FROM contents");
    } else {
        $stmt = $pdo->prepare("SELECT * FROM contents WHERE UserAccess = 1");
    }

    $stmt->execute();
    return $stmt->fetchAll(PDO::FETCH_ASSOC);
}
?>