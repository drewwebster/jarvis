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

// GET /api/conversations
app.get("/conversations/", (req, res) => {
  let query = admin.database().ref(`conversations/conversations_log`);
  return query
    .once("value")
    .then(snapshot => {
      let messages = [];
      snapshot.forEach(childSnapshot => {
        let message = childSnapshot.val();
        message.key = childSnapshot.key;
        messages.push(message);
      });
      return res.status(200).json(messages);
    })
    .catch(error => {
      console.log("Error getting messages", error.message);
      res.sendStatus(500);
    });
});

exports = module.exports = functions.https.onRequest(app);
