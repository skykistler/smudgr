function MenuCtrl($scope, $element, $attrs, $http) {
  $http.get("components/menu/menu.json")
    .then(function(response) {
      $scope.menuBar = response.data;
    }
  );
}

angular.module('smudgr').component('smudgrMenu', {
  templateUrl: "components/menu/menu.html",
  controller: MenuCtrl
});
