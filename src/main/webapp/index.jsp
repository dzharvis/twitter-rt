<!DOCTYPE html>
<html>
<head>
    <title>Hello WebSocket</title>
    <script src="web/js/sockjs-0.3.4.js"></script>
    <script src="web/js/stomp.js"></script>
    <link href="web/css/bootstrap.min.css" rel="stylesheet" media="screen">
    <script src="web/js/jquery-1.7.2.js"></script>
    <script src="web/js/client.js"></script>
</head>
<body style="background-color: #333; overflow: hidden">
<script src="web/js/bootstrap.min.js"></script>
<div align="center">
    <canvas height='528' width='1000' id='example'>looser</canvas>
    <div class="jumbotron" style="width: 75%; background-color: #333">
        <div class="container">
            <div class="result pull-left" style="position: relative;"></div>
        </div>
    </div>
    <div class="cc"
         style="align: left; width: 45%; background-color: rgba(70, 70, 70, 0); position: absolute">
        <div style="position: relative;" align="left" class="list"></div>
    </div>
</div>
</body>
</html>