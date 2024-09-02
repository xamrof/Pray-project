// const socket = io();

const sendEvent = async (word) => {
  try {
    const response = await fetch("http://127.0.0.1:3000/", {
      method: "POST",
      body: JSON.stringify({ word }),
      headers: {
        "Content-Type": "application/json",
      },
    });

    if (!response.ok) {
      throw new Error("Network response was not ok");
    }
  } catch (error) {
    console.error("sending event", error);
  }
};

const app_button = document.getElementById("app_button");
const service_button = document.getElementById("service_button");
const lock_button = document.getElementById("lock_screen");
const cancel_lock_screen = document.getElementById("cancel_lock_screen");

app_button.addEventListener("click", () => sendEvent("block_phone"));
service_button.addEventListener("click", () => sendEvent("service"));
lock_button.addEventListener("click", () => sendEvent("lock_screen"));
cancel_lock_screen.addEventListener("close", () => sendEvent("cancel_block"));
