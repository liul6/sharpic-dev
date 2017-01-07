sharpicApp.controller('saleController', function($rootScope, $http, $location, $route, $scope, DTOptionsBuilder, fileUpload) {
    $scope.clientName = null;
    $scope.auditDate = null;

    $scope.clientNames = [];
    $scope.auditDates = [];
    $scope.auditSales = [];
    $scope.auditSalesOptions = {};

    $scope.dtOptions = DTOptionsBuilder.newOptions()
        .withDisplayLength(100)
        .withOption('bLengthChange', false);

    $scope.getClientNames = function() {
        $http.get('/client/getClientNames')
            .success(function (data, status, headers, config) {
            $scope.clientNames = data;
            if($scope.clientNames.length>0) {
                $scope.populateDefault($scope.clientNames[0]);
            }
        })
        .error(function (data, status, header, config) {
        });
     };

    $scope.getClientNames();

    $scope.populateDefault = function(selectedClientName) {
        $scope.auditSales = [];

        $scope.clientName = selectedClientName;

        $scope.auditSalesOptions = {
            data: [],
            enableSorting: false,
            enableColumnMenus: false,
            columnDefs: [
                {name: 'recipe.recipeName', displayName: 'Recipe Name', width : '30%', enableSorting: false, enableSorting: false },
                {name: 'recipe.description', displayName: 'Recipe Items', type: 'number', width : '40%', enableSorting: false, enableSorting: false },
                {name: 'amount', displayName: 'Amount', type: 'number', enableSorting: false, enableSorting: false },
                {name: 'price', displayName: 'Price', type: 'number', enableSorting: false, enableSorting: false },
                {name: 'action', displayName: '', width : '3%', enableSorting: false, , enableSorting: false, cellTemplate: '<button class="btn btn-danger btn-xs" ng-click="grid.appScope.removeSale(row)"><span class="glyphicon glyphicon-remove"></span></button>' }
            ]
        };

        $scope.selectClient();
    };

    $scope.selectClient = function() {
        $scope.auditSales = [];
        $http.get('/client/getAuditDates?clientName=' + $scope.clientName)
            .success(function (data, status, headers, config) {
            $scope.auditDates = data;
            if($scope.auditDates.length>0) {
                $scope.auditDate = $scope.auditDates[0];
                $scope.selectAudit();
            }
        })
        .error(function (data, status, header, config) {
        });
    }

    $scope.selectAudit = function() {
       $http.get('/sale/getSales?auditDateStr=' + $scope.auditDate + '&clientName=' + $scope.clientName)
           .success(function (data, status, headers, config) {
           $scope.auditSales = data;
           $scope.auditSalesOptions.data = data;
       })
       .error(function (data, status, header, config) {
       });
    };

    $scope.removeSale = function(row) {
        var index = $scope.auditSalesOptions.data.indexOf(row.entity);
        $scope.auditSalesOptions.data.splice(index, 1);
    };

    $scope.uploadSaleFile = function(){
        var file = $scope.myFile;
        console.log('file is ' );
        console.dir(file);
        var uploadUrl = "/upload";

        var info = {'name': 'SALE', 'clientName': $scope.clientName, 'auditDateStr': $scope.auditDate};
        fileUpload.uploadFileToUrl(info, file, uploadUrl)
            .then(function (resp){
                $scope.auditSales = resp.data;
            });
    };

    $scope.uploadFile = function(files) {
        var fd = new FormData();
        //Take the first selected file
        fd.append('file', files[0]);
        fd.append('name', 'SALE');
        fd.append('clientName', $scope.clientName);
        fd.append('auditDateStr', $scope.auditDate);
        var uploadUrl = "/upload";

        var config = {
             headers : {
                 'Content-Type': 'application/x-www-form-urlencoded;charset=utf-8;'
             }
         }

        $http.post(uploadUrl, fd,
            {transformRequest: angular.identity,
             headers: {'Content-Type': undefined}
             }).then(function (resp){
                $scope.auditSales = resp.data;
                $scope.auditSalesOptions.data = resp.data;
             });
    };

});