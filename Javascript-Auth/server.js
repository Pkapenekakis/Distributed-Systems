
const express = require("express");
const cors = require("cors");
const app = express();






var corsOptions = {
  origin: "http://localhost:8081"
};

app.use(cors(corsOptions));

// parse requests of content-type - application/json
app.use(express.json());
// parse requests of content-type - application/x-www-form-urlencoded
app.use(express.urlencoded({ extended: true }));



const db = require("./models");
const Role = db.role;

db.sequelize.sync({force: true}).then(() => {
  console.log('Drop and Resync Db');
});






// simple route
app.get("/", (req, res) => {
  res.json({ message: "Welcome to the application." });
});
require('./routes/auth.routes')(app);
require('./routes/user.routes')(app);
// set port, listen for requests
const PORT = process.env.PORT || 8081;
app.listen(PORT, () => {
  console.log(`Server is running on port ${PORT}.`);
});



