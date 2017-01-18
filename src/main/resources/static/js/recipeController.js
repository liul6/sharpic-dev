sharpicApp.controller('recipeController', function($rootScope, $http, $location, $route, $scope, $modal) {
    $scope.clientName = null;
    $scope.clientNames = [];

    $scope.recipesOptions = {};
    $scope.clientProducts = [];

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
        // '<div><a href ng-click="modifyRecipe()">{{row.entity.description}}</a></div>'

        $scope.recipesOptions = {
            data: [],
            enableColumnMenus: false,
            columnDefs: [
                {name: 'recipeName', displayName: 'Recipe Name', width : '30%', enableCellEdit : false  },
                {name: 'description', displayName: 'Recipe Items', enableCellEdit : false, cellTemplate : descriptionCelltemplate },
                {name: 'action', displayName: '', width : '3%', cellTemplate: '<button class="btn btn-danger btn-xs" ng-click="grid.appScope.removeRecipe(row)"><span class="glyphicon glyphicon-remove"></span></button>' }
            ]
        };

        $scope.selectClient();
    };

    $scope.addRecipe = function() {
        var newRecipe = {recipeName : null, description : null};
        $scope.recipesOptions.data.unshift(newRecipe);
    };

    $scope.removeRecipe = function(row) {
        var index = $scope.recipesOptions.data.indexOf(row.entity);
        $scope.recipesOptions.data.splice(index, 1);
    };

    $scope.modifyRecipe = function(entity) {
        $scope.opts = {
            dialogFade: false,
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

        $scope.opts.resolve.clientProducts = function() {
            return $scope.clientProducts;
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
            $scope.clientProducts = data.model.clientProducts;
        })
        .error(function (data, status, header, config) {
        });
    };


})

var modifyRecipeController = function($scope, $http, $modalInstance, $modal, recipe, clientProducts) {
     $scope.recipe = recipe;
     $scope.clientProducts = clientProducts;

     $scope.productDescriptionCelltemplate = '<div><form name="inputForm"><input type="text" data-ng-model="MODEL_COL_FIELD" data-typeahead="description as clientProduct.description for clientProduct in grid.appScope.clientProducts | filter:$viewValue | limitTo:8" data-typeahead-on-select = "grid.appScope.typeaheadSelected(row.entity, $item)" class="form-control" ></form></div>';

     $scope.recipeItemOptions = {
        data: recipe.recipeItems,
        enableSorting: false,
        enableColumnMenus: false,
        columnDefs: [
           {name: 'clientProduct.description', displayName: 'Product', width : '40%', enableCellEdit : true, editableCellTemplate: $scope.productDescriptionCelltemplate },
            {name: 'fulls', displayName: 'Fulls', type: 'number', width : '9%' },
            {name: 'ounces', displayName: 'Ounces', type: 'number', width : '9%' },
            {name: 'action', displayName: '', width : '3%', cellTemplate: '<button class="btn btn-danger btn-xs" ng-click="grid.appScope.removeRecipeIem(row)"><span class="glyphicon glyphicon-remove"></span></button>' }
        ]
     };

    $scope.addRecipeItem = function() {
        var newRecipeItem = {clientProduct : null, fulls : null, ounces : null};
        $scope.recipeItemOptions.data.unshift(newRecipeItem);
    }

    $scope.removeRecipeIem = function(row) {
        var index = $scope.recipeItemOptions.data.indexOf(row.entity);
        $scope.recipeItemOptions.data.splice(index, 1);
    }

    $scope.typeaheadSelected = function(entity, selectedItem){
        entity.clientProduct = selectedItem;
        entity.productId = selectedItem.id;
        entity.recipeId = $scope.recipe.id;
        $scope.$broadcast('uiGridEventEndCellEdit');
    }

    $scope.saveRecipe = function () {
        var data = angular.toJson($scope.recipe);

        var config = {
            headers : {
                'Content-Type': 'application/json;'
            }
        }

//        $http.post('/client/saveRecipe', data, config)
        $http.post('/client/saveRecipe', data)
            .success(function (data, status, headers, config) {
                if(data != null) {
                    $scope.recipe = data;
                    $modalInstance.close(data);                }
        })
        .error(function (data, status, header, config) {
        });
    };

    $scope.close = function () {
        $modalInstance.dismiss('cancel');
    };
};
