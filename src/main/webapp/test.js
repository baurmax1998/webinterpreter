let oVal = jQuery.fn.val;
jQuery.fn.val = function (a) {
  let value = oVal.apply(this, arguments);
  if (a === undefined) //read val
    if (this.attr("type") === "checkbox") {
      return this.prop("checked");
    }
  else //write val
    if (this.attr("type") === "checkbox")
      this.prop("checked", a === "true")
  return value;
};

$(document).ready(load);
$(window).on('hashchange', load);

function load() {
  let hash = window.location.hash.substring(1);
  if (hash === "") {
    hash = "/person";
  }
  $.ajax({
    type: 'POST',
    url: hash,
    success: function (data) {
      data = JSON.parse(data)
      createUI2(data);
    },
    contentType: "application/json",
    dataType: 'json'
  });
  //let urlData = JSON.parse(decodeURI(hash));

}



var mapping = {
  "String": function (space, prop) {
    space.append(
      $("<label>").addClass("prop").attr("title", prop.description).text(prop.name)
    ).append(
      $("<input>").prop('readonly', !prop.editable).val(prop.content)
    ).append($("<br>"));
  },
  "Number": function (space, prop) {
    space.append(
      $("<label>").addClass("prop").attr("title", prop.description).text(prop.name)
    ).append(
      $("<input type='number'>").prop('readonly', !prop.editable).val(prop.content)
    ).append($("<br>"));
  },
  "Bool": function (space, prop) {
    space.append(
      $("<label>").addClass("prop").attr("title", prop.description).text(prop.name)
    ).append(
      $("<select>").prop('disabled', !prop.editable)
      .append($("<option>").text("true"))
      .append($("<option>").text("false"))
    ).append($("<br>"));
  }
};


function createUI2(data) {
  var interpreter = $("#interpreter").html("");
  console.log(data);
  interpreter.append($("<h3>").text(data.name))
    .append($("<div>").text(data.description))
  for (let i = 0; i < data.properties.length; i++) {
    const prop = data.properties[i];
    var type = prop.type;
    var createField = mapping[type];
    if (createField != undefined) {
      createField(interpreter, prop);
    } else {
      var complexType = type.split("/");
      if (complexType.includes("ref")) {
        if (complexType[0] == "list") {
          $.ajax({
            type: 'POST',
            url: "/" + type,
            success: function (data) {
              data = JSON.parse(data);
              var prettyData = [];
              for (let i = 0; i < data.length; i++) {
                const element = data[i];
                prettyData.push({
                  value: element
                });
              }
              var refInput = $("<input>").attr("id", "someid");
              interpreter.append(
                  $("<label>").addClass("prop").attr("title", prop.description).text(prop.name)
                ).append(refInput)
                .append($("<br>"));
              $("#someid").selectize({
                  items: [],
                  create: false,
                  mode: "multi",
                  duplicates: true,
                  enableDuplicate: true,
                  enableCreateDuplicate: true,
                  hideSelected: false,
                  valueField: 'value',
                  labelField: 'value',
                  searchField: 'value',
                  options: prettyData
                })
                .on('change', function () {
                  $(".item").hashColored();
                });
              $(".item").hashColored();
            },
            contentType: "application/json",
            dataType: 'json'
          });
        } else if (complexType[0] == "ref") {
          $.ajax({
            type: 'POST',
            url: "/" + type,
            success: function (data) {
              data = JSON.parse(data);
              var refInput = $("<select>");
              for (let x = 0; x < data.length; x++) {
                const element = data[x];
                refInput.append(
                  $("<option>").text(element)
                );
              }
              interpreter.append(
                  $("<label>").addClass("prop").attr("title", prop.description).text(prop.name)
                ).append(refInput)
                .append($("<br>"));

            },
            contentType: "application/json",
            dataType: 'json'
          });
        }
      } else if (complexType.includes("list")) {
        const listName = prop.name;
        $.ajax({
          type: 'POST',
          url: "/" + type,
          success: function (data) {
            data = JSON.parse(data);
            console.log(listName);
            interpreter.append($("<h3>").text(listName));
            var list = $("<div>").addClass("list");
            for (let i = 0; i < data.length; i++) {
              const element = data[i];

              var id;
              for (let x = 0; x < element.properties.length; x++) {
                const innerProp = element.properties[x];
                if (innerProp.name == element.idProperty)
                  id = innerProp.content;
              }
              list.append(
                $("<div>").addClass("elem")
                .append($("<a>").attr("href", "#/" + element.name + "/" + id).text(id))
                .append($("<br>"))
                .append($("<b>").text(element.name))
                .append($("<hr>"))
                .append($("<div>").text(JSON.stringify(element)))
              );
            }
            interpreter.append(list);


          },
          contentType: "application/json",
          dataType: 'json'
        });
      }
    }
  }
}


function createUI(urlData) {
  var className = urlData.type;
  var data = urlData.state;
  getCtClass(className, function (structure) {
    let card = $("<div class='w3-panel w3-card w3-light-grey'>").attr("className", className).append($("<h3>").text(className));
    addFields(card, className, structure, data);
    $("#interpreter").append(card);
  });
  /*$("#saveButton").on("click", function () {
      var toExecute = {
          clazz: className,
          executable: "save",
          object: JSON.stringify(getObject(className)),
          params: [],
          paramsClasses: []
      }
      execute(toExecute, function (data) {
          console.log(data);
      }, function () {
          toastr.error("Can't save the Clazz changes.", 'Unsaved!')
      });
  });*/
}