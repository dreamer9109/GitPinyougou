app.service('cartService', function ($http) {

    this.addGoodsToCartList = function (itemId, num) {

        return $http.get('./cart/addGoodsToCartList.do?itemId=' + itemId + '&num=' + num);

    }


    this.findCartList = function () {

        return $http.get('./cart/findCartList.do');

    }


    this.sum = function (cartList) {

        var totalValue = {"totalNum": 0, "totalFee": 0.00};

        for (var i = 0; i < cartList.length; i++) {
            for (var j = 0; j < cartList[i].orderItemList.length; j++) {
                totalValue.totalFee += cartList[i].orderItemList[j].totalFee;
                totalValue.totalNum += cartList[i].orderItemList[j].num;
            }
        }

        return totalValue;

    }


    //查询结算页地址
    this.findAddress=function () {
        return $http.get('./cart/findAddress.do');
    }


    this.submitOrder=function(order){
        return $http.post('order/add.do',order);
    }




})