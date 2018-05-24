<%@ page import="java.util.List" %>
<%--
  Created by IntelliJ IDEA.
  User: hpduy17
  Date: 1/21/15
  Time: 10:38 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <jsp:include page="head.jsp"/>
</head>
<body class="skin-blue">
<jsp:include page="header.jsp"/>
<div class="wrapper row-offcanvas row-offcanvas-left">
    <jsp:include page="left-side.jsp"/>
    <!-- Right side column. Contains the navbar and content of the page -->
    <aside class="right-side">
        <!-- Content Header (Page header) -->
        <section class="content-header">
            <h1>
                Home
                <small>it all starts here</small>
            </h1>
            <ol class="breadcrumb">
                <li class="active"><i class="fa fa-dashboard"></i> Home</li>
            </ol>
        </section>

        <!-- Main content -->
        <section class="content">
            <div class='row'>
                <div class='col-md-6'>
                    <div class='box box-success'>
                        <div class='box-header'>
                            <h3 class='box-title'>Angel Active Code Generator</h3>
                        </div>
                        <!-- /.box-header -->
                        <div class='box-body pad'>
                            <div class="form-group has-success">
                                <label class="control-label" for="numberOfCode"><i class="fa fa-check"></i>Number of
                                    code:</label>
                                <input type="text" class="form-control" id="numberOfCode" placeholder="Enter a number"/>
                            </div>
                            <button type="submit" class="btn btn-success" onclick='generate()'>Generate</button>
                        </div>
                    </div>

                    <div class='box box-success'>
                        <div class='box-header'>
                            <h3 class='box-title'>Push Notifications</h3>
                        </div>
                        <!-- /.box-header -->
                        <div class='box-body pad'>
                            <div class="form-group has-success">
                                <label class="control-label" for="numberOfCode"><i
                                        class="fa fa-check"></i>Title:</label>
                                <input type="text" class="form-control" id="title" placeholder="Enter title"/>
                                <label class="control-label" for="numberOfCode"><i
                                        class="fa fa-check"></i>Content:</label>
                                <input type="text" class="form-control" id="content" placeholder="Enter content"/>
                                <label class="control-label" for="numberOfCode"><i class="fa fa-check"></i>Link:</label>
                                <input type="text" class="form-control" id="link" placeholder="Enter link"/>
                                <label class="control-label" for="numberOfCode"><i class="fa fa-check"></i>Date:</label>
                                <input type="text" class="form-control" id="datetimepicker10"/>
                                <label class="control-label" for="numberOfCode"><i class="fa fa-check"></i>Type:</label>
                                <select id="type">
                                    <option value="1" default="true">Web View</option>
                                    <option value="2">Main Screen</option>
                                    <option value="3">Chat Screen</option>
                                    <option value="4">Profile Screen</option>
                                </select>
                            </div>
                            <button type="submit" class="btn btn-success" onclick='pushNotification()'>Push</button>
                        </div>
                    </div>
                    <div id="resultDiv">

                    </div>

                </div>
                <!-- /.col-->
            </div>
            <!-- ./row -->
        </section>
        <!-- /.content -->
    </aside>
    <!-- /.right-side -->
</div>
<!-- ./wrapper -->
<jsp:include page="footer.jsp"></jsp:include>
<script src="js/jquery.js"></script>
<script src="js/build/jquery.datetimepicker.full.js"></script>
<script type="text/javascript">
    $.datetimepicker.setLocale('en');
    $('#datetimepicker10').datetimepicker({
        step: 10
    });
    function generate() {
        var jsonData = '{"numberOfCode":' + document.getElementById('numberOfCode').value + '}';
        var apiName = "GetAngelActiveCodesForWebAPI";
        $.ajax({
            url: apiName,
            data: jsonData,
            type: 'POST',
            error: function () {
                alert("error occured!!!");
            },
            success: function (response) {
                console.log(response);
                $('#resultDiv').html(response)
            }
        });
    }

    function pushNotification() {
        var e = document.getElementById("type");
        var type = e.options[e.selectedIndex].value;
        var jsonData = '{"content":"' + document.getElementById('content').value +
                '","dates":"' + document.getElementById('datetimepicker10').value +
                '","link":"' + document.getElementById('link').value +
                '","title":"' + document.getElementById('title').value +
                '","type":' + type + '}';
        var apiName = "PushNotificationAPI";
        $.ajax({
            url: apiName,
            data: jsonData,
            type: 'POST',
            error: function () {
                alert("error occured!!!");
            },
            success: function (response) {
                console.log(response);
                $('#resultDiv').html(response)
            }
        });

    }
</script>
</body>
</html>
