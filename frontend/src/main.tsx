import { render } from "preact";

import * as socket from "@backend/socket.ts";

import App from "@ui/App.tsx";

// Configure the web socket.
socket.setup();

// Render the interface.
render(<App />, document.body);
