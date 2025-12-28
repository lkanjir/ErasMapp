import { initializeApp, cert } from "firebase-admin/app";
import { getAuth } from "firebase-admin/auth";
import fs from "fs";

const [_, __, uid, role] = process.argv;
if (!uid || !role) {
    console.error("Usage: node scripts/setCustomClaims.mjs <uid> <admin|student>");
    process.exit(1);
}

const serviceAccountPath = process.env.GOOGLE_APPLICATION_CREDENTIALS;
if (!serviceAccountPath || !fs.existsSync(serviceAccountPath)) {
    console.error("Set the environment variable GOOGLE_APPLICATION_CREDENTIALS to your service account JSON path.");
    process.exit(1);
}

const serviceAccount = JSON.parse(fs.readFileSync(serviceAccountPath, "utf8"));

const app = initializeApp({ credential: cert(serviceAccount) });
await getAuth(app).setCustomUserClaims(uid, { role });

console.log(`Set custom claim role=${role} for uid=${uid}`);