<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE HTML>
<html>
<head>
    <title>Mandelbrot set</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <style>
        img {
            display: block
        }
    </style>
</head>

<body>
<c:choose>
    <c:when test="${calculated}">
        <c:forEach items="${results}" var="r">

        <img alt="Mandelbrot set: (${r.left}, ${r.top}i) to (${r.right}, ${r.bottom}i). Resolution: ${r.width}x${r.height}. Iterations: ${r.precision}"
             src="data:image/png;base64,${r.image}"/>
        </c:forEach>

    </c:when>
    <c:otherwise>
        Wait for the result
    </c:otherwise>
</c:choose>

</body>
</html>
