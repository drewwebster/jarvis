// Helpers to interacte with dialogflow
const functions = require("firebase-functions");
const dialogFlow = require("apiai")(functions.config().apiai.clienttoken);

module.exports = {
  fulfillmentResponse: function(query) {
    return new Promise(function(resolve, reject) {
      dialogFlow
        .textRequest(query, {
          sessionId: "session"
        })
        .on("response", function(response) {
          console.log(response);
          const fulfillment = response.result.fulfillment.speech;
          if (fulfillment) {
            resolve(fulfillment);
          } else {
            reject(new Error("Did not get response"));
          }
        })
        .on("error", function(error) {
          reject(error);
        })
        .end();
    });
  }
};
