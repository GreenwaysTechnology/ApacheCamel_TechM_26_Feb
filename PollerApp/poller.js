const fs = require("fs");
const path = require("path");

// Folder where Camel reads files
const inputDir = path.join(__dirname, "../fileprocessing-camelspring/data/input");

// Ensure folder exists
fs.mkdirSync(inputDir, { recursive: true });

function randomInt(min, max) {
    return Math.floor(Math.random() * (max - min)) + min;
}

function randomTransaction() {
    const id = "TX" + randomInt(1000, 9999);
    const acc = "ACC" + randomInt(10000, 99999);
    const type = Math.random() > 0.5 ? "WITHDRAW" : "DEPOSIT";
    const amount = randomInt(100, 10000);

    return `${id},${acc},${type},${amount}`;
}

function createFile() {
    const rows = randomInt(3, 8);

    let content = "txnId,account,type,amount\n";

    for (let i = 0; i < rows; i++) {
        content += randomTransaction() + "\n";
    }

    const fileName = `txn_${Date.now()}.csv`;
    const filePath = path.join(inputDir, fileName);

    fs.writeFileSync(filePath, content);

    console.log("Generated:", fileName);
}

function scheduleNext() {
    createFile();

    const nextDelay = randomInt(2000, 6000);
    setTimeout(scheduleNext, nextDelay);
}

console.log("Starting file generator...");
scheduleNext();
