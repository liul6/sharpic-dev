sharpicApp.controller('saleController', function($rootScope, $http, $location, $route, $scope, DTOptionsBuilder, fileUpload) {
    $scope.clientName = null;
    $scope.auditDate = null;

    $scope.clientNames = [];
    $scope.auditDates = [];
    $scope.auditSales = [];

    $scope.dtOptions = DTOptionsBuilder.newOptions()
        .withDisplayLength(25)
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
        $scope.auditEntries = [];

        $scope.clientName = selectedClientName;
        $scope.selectClient();
    };

    $scope.selectClient = function() {
        $scope.auditEntries = [];
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
       })
       .error(function (data, status, header, config) {
       });
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
             });
    };

});