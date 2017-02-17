sharpicApp.controller('recipeController', function($rootScope, $http, $location, Notify, $route, $scope, $modal) {
    $scope.clientName = null;
    $scope.clientNames = [];

    $scope.recipesOptions = {};
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
        var descriptionCelltemplate = '<div ng-click="grid.appScope.modifyRecipe(row.entity)"><a href="">{{row.entity.description}}</a></div>';

        $scope.recipesOptions = {
            data: [],
            enableColumnMenus: false,
            columnDefs: [
                {name: 'recipeName', displayName: 'Recipe Name', width : '30%', enableCellEdit : true  },
                {name: 'description', displayName: 'Recipe Items', enableCellEdit : false, cellTemplate : descriptionCelltemplate },
                {name: 'action', displayName: '', width : '4%', cellTemplate: '<button class="btn btn-danger btn-xs" ng-click="grid.appScope.removeRecipe(row)"><span class="glyphicon glyphicon-remove"></span></button>' }
            ]
        };

        $scope.selectClient();
    };

    $scope.addRecipe = function() {
        var newRecipe = {recipeName : null, recipeItems : [], description : '***UNDEFINED***'};
        $scope.recipesOptions.data.unshift(newRecipe);
    };

    $scope.removeRecipe = function(row) {
        var index = $scope.recipesOptions.data.indexOf(row.entity);
        $scope.recipesOptions.data.splice(index, 1);
    };

    $scope.recipesOptions.onRegisterApi = function(gridApi){
        $scope.gridApi = gridApi;

        gridApi.edit.on.afterCellEdit($scope,function(rowEntity, colDef, newValue, oldValue){
            if(newValue!=oldValue) {
                $scope.saveRecipe(rowEntity);
            }

            $scope.apply();
        });
    };

    $scope.saveRecipe = function(recipe) {
        if(recipe.recipeName==null)
            return;
        recipe.clientName = $scope.clientName;

       var data = angular.toJson(recipe);

       var config = {
           headers : {
               'Content-Type': 'application/json;'
           }
       }

       $http.post('/client/saveRecipe', data)
           .success(function (data, status, headers, config) {
           if(data.successful) {
                Notify.addMessage('Recipe saved successfully', 'success');
                recipe = data.model.recipe;
           }
           else {
                Notify.addMessage('Recipe failed to save: ' + data.errorText, 'danger');
           }
       })
       .error(function (data, status, header, config) {
            Notify.addMessage('Recipe failed to save: ' + data.errorText, 'danger');
       });
    }

    $scope.modifyRecipe = function(entity) {
        $scope.opts = {
            dialogFade: true,
            keyboard: true,
            templateUrl : 'pages/modifyRecipe.html',
            controller : modifyRecipeController,
            size : 'lg',
            resolve: {

            } // empty storage
        };

        $scope.opts.resolve.recipe = function() {
            return angular.copy(entity); // {recipeName : entity.recipeName} pass name to Dialog
        }

        $scope.opts.resolve.products = function() {
            return $scope.products;
        }

        var modalInstance = $modal.open($scope.opts);

        modalInstance.result.then(function(recipe){
            entity.recipeItems = recipe.recipeItems;
            entity.description = recipe.description;
        },function(){
            //on cancel button press
            console.log("Modal Closed");
        });
    };

    $scope.selectClient = function() {
        $http.get('/client/getClientRecipes?clientName=' + $scope.clientName)
            .success(function (data, status, headers, config) {
            $scope.recipesOptions.data = data.model.clientRecipes;
            $scope.products = data.model.products;
        })
        .error(function (data, status, header, config) {
        });
    };

})

var modifyRecipeController = function($scope, $http, $modalInstance, $modal, recipe, products) {
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

    $scope.saveRecipe = function () {
        $scope.recipe.recipeItems = $scope.recipeItemOptions.data;
        var data = angular.toJson($scope.recipe);

        var config = {
            headers : {
                'Content-Type': 'application/json;'
            }
        }

//        $http.post('/client/saveRecipe', data, config)
        $http.post('/client/saveRecipe', data)
            .success(function (data, status, headers, config) {
                if(data != null && data.successful) {
                    $scope.recipe = data.model.recipe;
                    $modalInstance.close(data);
                    Notify.addMessage('Recipe saved successfully', 'success');
                }
                else  {
                     Notify.addMessage('Recipe failed to save: ' + data.errorText, 'danger');
                }
        })
        .error(function (data, status, header, config) {
             Notify.addMessage('Recipe failed to save: ' + data.errorText, 'danger');
        });
    };

    $scope.close = function () {
        $modalInstance.dismiss('cancel');
    };
};
