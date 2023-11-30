$(function(){
	$("#publishBtn").click(publish);
});

function publish() {
	$("#publishModal").modal("hide");

	//获取标题和内容
	var title = $("#recipient-name").val();
	var content = $("#message-text").val();
	//判断标题内容是否为空
	if(title == null|| title==""){
		alert("标题不能为空！")
		return;
	}
	if(content == null|| content==""){
		alert("内容不能为空！")
		return;
	}

	// 发送异步请求
	$.post(
		CONTEXT_PATH + "/discuss/add",
		{"title":title, "content":content},
		function (data){
			data = $.parseJSON(data);
			// 显示提示消息
			$("#hintBody").text(data.msg);
			// 显示提示框
			$("#hintModal").modal("show");
			// 2秒后，自动隐藏提示框
			setTimeout(function(){
				$("#hintModal").modal("hide");
				// 刷新页面
				if(data.code == 0){
					$("#hintModal").modal("hide");
					window.location.reload();
				}
			}, 2000);
		}

	);

}