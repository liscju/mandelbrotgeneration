<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE HTML>
<html>
<head>
    <title>Mandelbrot set</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
</head>

<body>
<c:choose>
    <c:when test="${calculated}">
        <img alt="Mandelbrot set: (${left}, ${top}i) to (${right}, ${bottom}i). Resolution: ${width}x${height}. Iterations: ${precision}"
             src="data:image/png;base64,${renderedImage}"/>
    </c:when>
    <c:otherwise>
        Wait for the result
    </c:otherwise>
</c:choose>

</body>
</html>
