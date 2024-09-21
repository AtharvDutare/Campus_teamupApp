const { onRequest } = require("firebase-functions/v2/https");
const admin = require("firebase-admin");
const logger = require("firebase-functions/logger");

admin.initializeApp();

exports.sendNotification = onRequest(async (req, res) => {
  logger.info("Received request to send notification", { requestBody: req.body });

  // Extract request body
  const { token, title, body, senderId, senderImage } = req.body;

  // Log request payload type and contents
  logger.info("Request Payload Details", {
    payloadType: typeof req.body,
    payloadContent: req.body
  });

  // Validate request body
  if (!token || !title || !body || !senderId || !senderImage) {
    logger.warn("Missing one or more required fields", {
      token,
      title,
      body,
      senderId,
      senderImage
    });
    return res.status(400).send('Missing token, title, body, senderId, or senderImage');
  }

  logger.info("All required fields present", {
    token,
    title,
    body,
    senderId,
    senderImage
  });

  const message = {
    notification: {
      title: title,
      body: body,
    },
    data: {
      senderId: senderId,
      senderImage: senderImage,
    },
    token: token,
  };

  try {
    logger.info("Sending notification", { message });

    await admin.messaging().send(message);

    logger.info("Notification sent successfully", { token, title, senderId });
    res.status(200).send('Notification sent successfully');
    
  } catch (error) {
    logger.error("Error sending notification", {
      error: error.message,
      stack: error.stack,
      message: message
    });
    res.status(500).send('Error sending notification');
  }
});
