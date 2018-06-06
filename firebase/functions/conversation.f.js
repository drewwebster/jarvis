/**
 * Contains coversation related functions
 */
const functions = require("firebase-functions");
const admin = require("firebase-admin");
try {
  admin.initializeApp();
} catch (e) {
  console.log(e);
}
const express = require("express");
const app = express();
const utils = require("./util/utils");

// Express middleware that validates Firebase ID Tokens passed in the Authorization HTTP header.
// The Firebase ID token needs to be passed as a Bearer token in the Authorization HTTP header like this:
// `Authorization: Bearer <Firebase ID Token>`.
// when decoded successfully, the ID Token content will be added as `req.user`.
const authenticate = (req, res, next) => {
  if (
    !req.headers.authorization ||
    !req.headers.authorization.startsWith("Bearer ")
  ) {
    res.status(403).send("Unauthorized");
    return;
  }
  const idToken = req.headers.authorization.split("Bearer ")[1];
  admin
    .auth()
    .verifyIdToken(idToken)
    .then(decodedIdToken => {
      req.user = decodedIdToken;
      return next();
    })
    .catch(() => {
      res.status(403).send("Unauthorized");
    });
};

// app.use(authenticate);

// Creates new conversation entry in database, hits DialogFlow to determine a response and makes
// an answer entry
app.post("/conversations/", async (req, res) => {
  try {
    const query = req.body.query;
    if (query) {
      const conversation = {
        content: utils.capitalize(query.trim()),
        who: "user",
        timestamp: admin.database.ServerValue.TIMESTAMP
      };

      let conversationLog = admin
        .database()
        .ref(`conversations/conversationsLog`);

      await conversationLog.push(conversation); // Push to conversation log
      res.status(201).send(
        JSON.stringify({
          data: {}
        })
      );
    } else {
      res.status(500).send("Missing query");
    }
  } catch (error) {
    console.error(error);
    res.sendStatus(500);
  }
});

exports = module.exports = functions.https.onRequest(app);
