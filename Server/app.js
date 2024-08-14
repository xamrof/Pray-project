const express = require("express");
const http = require("http");
const socketIO = require("socket.io");

const app = express();
const server = http.createServer(app);
const io = socketIO(server);

app.use(express.static("public"));
app.use(express.json());

io.on("connection", (socket) => {
  console.log("connected " + socket.id);
  socket.on("message", (msg) => {
    console.log(msg + " i do it!!!");
  });

  socket.on("touch", (msg) => {
    socket.emit("messageServer", msg);
  });

  socket.on("disconnect", () => {
    console.log(socket.id + " disconnected");
  });
});

app.get("/", (req, res) => {
  res.sendFile(path.join(__dirname, "public", "index.html"));
});

app.post("/", async (req, res) => {
  const { word } = req.body;
  console.log(`message sended ${word}`);

  io.emit("messageServer", word);

  res.status(200);
});

server.listen(3000, () => {
  console.log("Server listen on port 3000");
});
