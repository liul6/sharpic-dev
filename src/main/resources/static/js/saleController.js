sharpicApp.controller('saleController', function($rootScope, $http, $location, $route, $scope, $modal, Notify, fileUpload) {
    $scope.clientName = null;
    $scope.auditDate = null;
    $scope.audit = null;

    $scope.clientNames = [];
    $scope.auditDates = [];
    $scope.auditSalesOptions = {};
    $scope.clientRecipes = [];
    $scope.products = [];

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
        $scope.clientName = selectedClientName;
        $scope.selectClient();
    };

    $scope.selectClient = function() {
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

    $scope.auditSalesOptions.onRegisterApi = function(gridApi){
        $scope.gridApi = gridApi;

        gridApi.edit.on.afterCellEdit($scope,function(rowEntity, colDef, newValue, oldValue){
            if(newValue!=oldValue) {
                $scope.saveRow(rowEntity);
            }

            $scope.apply();
        });
    };

    $scope.selectAudit = function() {
       $http.get('/sale/getAuditInfo?auditDateStr=' + $scope.auditDate + '&clientName=' + $scope.clientName)
           .success(function (data, status, headers, config) {
            $scope.audit = data.model.audit;
            $scope.clientRecipes = data.model.recipes;
            $scope.products = data.model.products;

            var descriptionCelltemplate = '<div ng-click="grid.appScope.modifyAuditRecipe(row.entity)"><a href="">{{row.entity.recipe.description}}</a></div>';
            $scope.recipeCelltemplate = '<div><form name="inputForm"><input type="text" data-ng-model="MODEL_COL_FIELD" data-typeahead="name as recipe.recipeName for recipe in grid.appScope.clientRecipes | filter:$viewValue | limitTo:8" data-typeahead-on-select = "grid.appScope.typeaheadSelected(row.entity, $item)" class="form-control" ></form></div>';

            $scope.auditSalesOptions = {
                data: data.model.sales,
                enableRowSelection: false,
                enableCellEditOnFocus: true,
                multiSelect: false,
                enableColumnMenus: false,
                columnDefs: [
                    {name: 'recipe.recipeName', displayName: 'Recipe Name', width : '30%', enableCellEdit : true, editableCellTemplate: $scope.recipeCelltemplate },
                    {name: 'recipe.description', displayName: 'Recipe Items', width : '50%', enableCellEdit : false, cellTemplate : descriptionCelltemplate },
                    {name: 'amount', displayName: 'Amount', type: 'number', width : '8%' },
                    {name: 'price', displayName: 'Price', type: 'number', width : '8%' },
                    {name: 'action', displayName: '', width : '4%', cellTemplate: '<button class="btn btn-danger btn-xs" ng-click="grid.appScope.removeSale(row)"><span class="glyphicon glyphicon-remove"></span></button>' }
                ]
            };
       })
       .error(function (data, status, header, config) {
       });
    };

   $scope.saveRow = function(sale) {
        if(sale.price==null || sale.amount == null || sale.recipe ==null)
            return;

        if(sale.recipeId ==null)
            sale.recipeId = sale.recipe.id;

        if(sale.auditId ==null)
            sale.auditId = $scope.audit.id;

       var data = angular.toJson(sale);

       var config = {
           headers : {
               'Content-Type': 'application/json;'
           }
       }

       $http.post('/sale/saveSale', data)
           .success(function (data, status, headers, config) {
           if(data.successful)
                Notify.addMessage('Sale saved successfully', 'success');
           else
                Notify.addMessage('Sale failed to save: ' + data.errorText, 'danger');
       })
       .error(function (data, status, header, config) {
            Notify.addMessage('Sale failed to save: ' + data.errorText, 'danger');
       });
   }

   $scope.modifyAuditRecipe = function(entity) {
        $scope.opts = {
            dialogFade: false,
            keyboard: true,
            templateUrl : 'pages/modifyAuditRecipe.html',
            controller : modifyAuditRecipeController,
            size : 'lg',
            resolve: {

            } // empty storage
        };

        $scope.opts.resolve.recipe = function() {
            return angular.copy(entity.recipe);
        }

        $scope.opts.resolve.products = function() {
            return $scope.products;
        }

        var modalInstance = $modal.open($scope.opts);

        modalInstance.result.then(function(recipe){
            entity.recipe.recipeItems = recipe.recipeItems;
            entity.recipe.description = recipe.description;
        },function(){
            //on cancel button press
            console.log("Modal Closed");
        });
    };


    $scope.typeaheadSelected = function(entity, selectedItem){
        entity.recipe = selectedItem;
        $scope.$broadcast('uiGridEventEndCellEdit');
    };

    $scope.addSale = function() {
        var newSale = {recipe : { recipeName : null, description : null}, amount : null, price : null };
        $scope.auditSalesOptions.data.unshift(newSale);
    };

    $scope.removeSale = function(row) {
        var data = angular.toJson(row.entity);

        var config = {
            headers : {
                'Content-Type': 'application/json;'
            }
        }

        $http.post('/sale/deleteSale', data)
            .success(function (data, status, headers, config) {
                if(data != null && data.successful) {
                    var index = $scope.auditSalesOptions.data.indexOf(row.entity);
                    $scope.auditSalesOptions.data.splice(index, 1);
                }
        })
        .error(function (data, status, header, config) {
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

        $scope.auditSalesOptions.data = [];

        $http.post(uploadUrl, fd,
            {transformRequest: angular.identity,
             headers: {'Content-Type': undefined}
             }).then(function (resp){
                $scope.auditSalesOptions.data = resp.data.model.sales;
             });
    };

})

var modifyAuditRecipeController = function($scope, $http, $modalInstance, $modal, recipe, products) {
     $scope.recipe = recipe;
     $scope.products = products;

     $scope.productDescriptionCelltemplate = '<div><form name="inputForm"><input type="text" data-ng-model="MODEL_COL_FIELD" data-typeahead="description as product.description for product in grid.appScope.products | filter:$viewValue | limitTo:8" data-typeahead-on-select = "grid.appScope.typeaheadSelected(row.entity, $item)" class="form-control" ></form></div>';

     $scope.recipeItemOptions = {
        data: recipe.recipeItems,
        enableSorting: false,
        enableColumnMenus: false,
        columnDefs: [
           {name: 'product.description', displayName: 'Product', width : '40%', enableCellEdit : true, editableCellTemplate: $scope.productDescriptionCelltemplate },
            {name: 'fulls', displayName: 'Fulls', type: 'number', width : '9%' },
            {name: 'ounces', displayName: 'Ounces', type: 'number', width : '9%' },
            {name: 'action', displayName: '', width : '4%', cellTemplate: '<button class="btn btn-danger btn-xs" ng-click="grid.appScope.removeRecipeIem(row)"><span class="glyphicon glyphicon-remove"></span></button>' }
        ]
     };

    $scope.addRecipeItem = function() {
        var newRecipeItem = {product : null, fulls : null, ounces : null};
        $scope.recipeItemOptions.data.unshift(newRecipeItem);
    }

    $scope.removeRecipeIem = function(row) {
        var index = $scope.recipeItemOptions.data.indexOf(row.entity);
        $scope.recipeItemOptions.data.splice(index, 1);
    }

    $scope.typeaheadSelected = function(entity, selectedItem){
        entity.product = selectedItem;
        entity.productId = selectedItem.id;
        entity.recipeId = $scope.recipe.id;
        $scope.$broadcast('uiGridEventEndCellEdit');
    }

    $scope.saveRecipeFully = function () {
        $scope.recipe.recipeItems = $scope.recipeItemOptions.data;
        var data = angular.toJson($scope.recipe);

        var config = {
            headers : {
                'Content-Type': 'application/json;'
            }
        }

        $http.post('/sale/saveRecipeFully', data)
            .success(function (data, status, headers, config) {
                if(data != null) {
                    if(data.successful) {
                        $scope.recipe = data.model.auditRecipe;
                        $modalInstance.close(data);
                         Notify.addMessage('Audit/Master Recipe saved successfully', 'success');
                    }
                    else {
                        Notify.addMessage('Audit/Master Recipe failed to save: ' + data.errorText, 'danger');
                    }
                }
        })
        .error(function (data, status, header, config) {
            Notify.addMessage('Audit/Master Recipe failed to save: ' + data.errorText, 'danger');
        });
    };

    $scope.saveAuditRecipe = function () {
        $scope.recipe.recipeItems = $scope.recipeItemOptions.data;
        var data = angular.toJson($scope.recipe);

        var config = {
            headers : {
                'Content-Type': 'application/json;'
            }
        }

        $http.post('/sale/saveAuditRecipe', data)
            .success(function (data, status, headers, config) {
                if(data != null) {
                    if(data.successful) {
                        $scope.recipe = data.model.auditRecipe;
                        $modalInstance.close(data);
                         Notify.addMessage('Audit Recipe saved successfully', 'success');
                    }
                    else {
                        Notify.addMessage('Audit Recipe failed to save: ' + data.errorText, 'danger');
                    }
                }
        })
        .error(function (data, status, header, config) {
            Notify.addMessage('Audit Recipe failed to save: ' + data.errorText, 'danger');
        });
    };

    $scope.close = function () {
        $modalInstance.dismiss('cancel');
    };
};
