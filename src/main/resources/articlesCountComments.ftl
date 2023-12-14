<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Главная страница</title>
    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/gh/yegor256/tacit@gh-pages/tacit-css-1.6.0.min.css"/>
</head>

<body>

<h1>Список статей с количеством комментариев</h1>
<table>
    <tr>
        <th>Название статьи</th>
        <th>Количество комментариев</th>
    </tr>
    <#list articles as article>
        <tr>
            <td>${article.name}</td>
            <td>${article.countComments}</td>
        </tr>
    </#list>
</table>

</body>

</html>