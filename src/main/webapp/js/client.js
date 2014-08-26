var stompClient = null;
var dataHash = {};
var attached = false;

function disconnect() {
    stompClient.disconnect();
    console.log("Disconnected");
}

function handleTweetEvent(message) {
    var data = JSON.parse(message.body);
    var coord = convertCoordinates(data);
    doTwitSpot(coord);
    displayCurrentTwit(data);
    var hash = getHash(coord.x, coord.y);
    if (dataHash[hash] == null) {
        dataHash[hash] = [];
    }
    dataHash[hash].push(data);
}
function getMousePos(canvas, evt) {
    var rect = canvas.getBoundingClientRect();
    return {
        x: evt.clientX - rect.left,
        y: evt.clientY - rect.top
    };
}
function getHash(x, y) {
    var hash = (x * 1000) + y;
    hash = hash % 1000151;
    return hash;
}
function displayTwits(canvas, evt) {
    var mousePos = getMousePos(canvas, evt);
    var _x = mousePos.x;
    var _y = mousePos.y;
    _x = Math.floor(_x);
    _y = Math.floor(_y);
    var htm = '';
    for (var x = _x - 5; x < _x + 5; x++) {
        for (var y = _y - 5; y < _y + 5; y++) {
            var hash = getHash(x, y);
            var dataList = dataHash[hash];
            if (dataHash[hash] != null) {
                for (var i = 0; i < dataList.length; i++) {
                    htm = htm
                        + "<div style=\"background-color: rgba(70, 70, 70, 0.5)\" class=\"well-sm\"><h3><small>"
                        + dataList[i].text
                        + "</h3></small></div><br>";
                }
            }
        }
    }
    if (htm.length >= 0) {
        $(".list").html(htm);
        $(".cc").css({
            top: evt.clientY + 20,
            left: evt.clientX + 20,
            opacity: 1
        });
    } else {
        $(".list").html("");
        $(".cc").css({
            opacity: 0,
            width: 0,
            height: 0
        });
    }
}
function doTwitSpot(coord) {
    var x = coord.x;
    var y = coord.y;
    var example = document.getElementById("example");
    var context = example.getContext('2d');
    context.beginPath();
    context.arc(x, y, 10, 0, 2 * Math.PI);
    var grd = context.createRadialGradient(x, y, 1, x, y, 5);
    grd.addColorStop(0, "rgba(255, 255, 255, 1)");
    grd.addColorStop(0.05, "rgba(255, 255, 255, 0.1)");
    grd.addColorStop(1, "rgba(255, 255, 255, 0.0)");
    context.fillStyle = grd;
    context.fill();

    for (var _x = x - 30; _x < x + 30; _x++) {
        for (var _y = y - 30; _y < y + 30; _y++) {
            var hash = getHash(_x, _y);
            if (dataHash[hash] != null) {
                context.beginPath();
                context.strokeStyle = "rgba(255, 255, 255, .2)";
                context.lineWidth = .1;
                context.moveTo(x, y);
                context.lineTo(_x, _y);
                context.stroke();
                console.log("123")
            }
        }
    }
}
function displayCurrentTwit(data) {
    $(".result")
        .animate(
        {
            top: "+="
                + ($(".well-sm")
                .height() + 15)
                + "px"
        },
        "fast",
        function () {
            var result = $(".result");
            result.css({
                top: "0px"
            }, "fast");
            result
                .html(
                    "<div class='well-sm pull-left block' style='opacity: 0'><h3><small class='pull-left'>"
                        + data.text
                        + "</small></h3></div>");

            $(".block").animate({
                opacity: 1
            });
        });
}
function convertCoordinates(data) {
    var x = (data.x + 170.0) / 360.0;
    var y = (85.0 - data.y) / 145.0;
    x = x * 1000.0;
    y = y * 528.0;
    x = Math.floor(x);
    y = Math.floor(y);
    return {x: x, y: y};
}
var handler = function (evt) {
    var canvas = document.getElementById("example");
    displayTwits(canvas, evt);
};
function attachEventListener() {
    if (!attached) {
        $("#example").bind('mousemove', handler);
        attached = true;
    }
}
$(document).ready(function () {
    var canvas = document.getElementById("example");
    var ctx = canvas.getContext('2d');
    var pic = new Image();
    pic.src = 'web/map_d.png';
    pic.onload = function () {
        ctx.drawImage(pic, 0, 0, 1000, 528);
        connect();
    };

    attachEventListener();
    $(document).click(function () {
        if (attached) {
            $("#example").unbind();
            attached = false;
        } else {
            attachEventListener();
        }
    });
});