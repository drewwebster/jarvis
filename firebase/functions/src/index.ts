import * as functions from 'firebase-functions';

// Start writing Firebase Functions
// https://firebase.google.com/docs/functions/typescript

const MAX_TEMPERATURE_LOG_COUNT = 35

const TEMPERATURE_LOG_PATH = "/pi/health/temperature_log/{pushId}"

export const helloWorld = functions.https.onRequest((request, response) => {
    response.send("Hello from Firebase!");
});