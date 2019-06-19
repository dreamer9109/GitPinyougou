app.controller('payController', function ($scope,$location,payService) {



    $scope.createNative=function () {

        payService.createNative().success(
            function (response) {

                $scope.out_trade_no=response.out_trade_no;
                $scope.total_fee=(response.total_fee/100).toFixed(2);

                //生成二维码
                var qr = new QRious({

                    element:document.getElementById("qrious"),
                    size:250,
                    value:response.url,
                    level:"H"

                });


                //生成二维码之后监控支付状态

                checkPayStatus(response.out_trade_no);
            }
        )

    }


    //监控用户支付状态返回相应信息123
   checkPayStatus=function (out_trade_no) {

        payService.checkPayStatus(out_trade_no).success(

            function (response) {

                if (response.success){

                    location.href="paysuccess.html#?money="+$scope.total_fee;
                } else{

                    if (response.message=="二维码超时"){
                        scope.createNative();
                    }else {
                        location.href='payfail.html';
                    }

                }

            }
        )
    }

    //获得跳转到成功页面携带的参数
    $scope.getMoney=function () {
        return location.search()["money"];
    }


})