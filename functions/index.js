const { onRequest } = require("firebase-functions/v2/https");
const admin = require("firebase-admin");
const logger = require("firebase-functions/logger");

admin.initializeApp();

exports.sendNotification = onRequest(async (req, res) => {
  const { token, title, body } = req.body;

  // Validate request body
  if (!token || !title || !body) {
    return res.status(400).send('Missing token, title, or body');
  }

  const message = {
    notification: {
      title: title,
      body: body,
    },
    token: token,
  };

  try {
    await admin.messaging().send(message);
    res.status(200).send('Notification sent successfully');
  } catch (error) {
    logger.error('Error sending notification:', error);
    res.status(500).send('Error sending notification');
  }
});
