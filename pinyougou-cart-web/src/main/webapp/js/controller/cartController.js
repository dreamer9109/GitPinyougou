app.controller('cartController', function ($scope, cartService,payService) {

    $scope.addGoodsToCartList = function (itemId, num) {

        cartService.addGoodsToCartList(itemId, num).success(
            function (response) {

                if (response.success) {
                    $scope.findCartList();//刷新列表
                } else {
                    alert(response.message);//弹出错误提示
                }
            }
        )

    }


    $scope.findCartList = function () {

        cartService.findCartList().success(
            function (response) {
                $scope.cartList = response;
                $scope.totalValue = cartService.sum($scope.cartList);//求合计数
            }
        )

    }


    //查询登陆入结算页地址
    $scope.findAddress = function () {

        cartService.findAddress().success(
            function (response) {

                $scope.addressList = response;

                //设置默认选中地址
                for (var i = 0; i < $scope.addressList.length; i++) {
                    if ($scope.addressList[i].isDefault == '1') {
                        $scope.address = $scope.addressList[i];
                        break;
                    }
                }

            }
        )

    }


    //判断地址是否被选中
    $scope.isSelectAddress = function (adress) {


        if (adress == $scope.address) {
            return true;
        } else {
            return false;
        }
    }

    //设置地址选中样式
    $scope.selectAddress = function (address) {

        $scope.address = address;

    }


    $scope.submitOrder = function () {
        $scope.order.receiverAreaName = $scope.address.address;//地址
        $scope.order.receiverMobile = $scope.address.mobile;//手机
        $scope.order.receiver = $scope.address.contact;//联系人
        cartService.submitOrder($scope.order).success(
            function (response) {
                if (response.success) {//页面跳转
                    if ($scope.order.paymentType == '1') {//如果是微信支付，跳转到支付页面
                        location.href = "pay.html";
                    } else {//如果货到付款，跳转到提示页面
                        location.href = "paysuccess.html";
                    }
                } else {
                    alert(response.message); //也可以跳转到提示页面
                }
            }
        );
    }




})