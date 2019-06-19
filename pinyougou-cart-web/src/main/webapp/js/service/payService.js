app.service('payService', function ($http) {

    //提交订单到微信支付端获得二维码
    this.createNative = function () {

        return $http.get('./pay/createNative.do');

    }

    //监控用户支付状态返回相应信息
    this.checkPayStatus = function (out_trade_no) {

        return $http.get('./pay/checkPayStatus.do?out_trade_no='+out_trade_no);

    }


})