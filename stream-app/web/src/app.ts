import { LitElement, css, html } from "lit";
import { customElement } from "lit/decorators.js";
import "./parallel-component";
import "./stream-component";

@customElement("my-app")
export class App extends LitElement {
  constructor() {
    super();
  }
  static styles = css`
    .parent {
      display: flex;
    }
    .container {
      padding: 10px;
    }
  `;

  render() {
    return html`
      <div class="parent">
        <div class="container">
          <stream-component></stream-component>
        </div>
        <!-- <div class="container">
          <parallel-component></parallel-component>
        </div> -->
      </div>
    `;
  }

  async connectedCallback() {
    super.connectedCallback();
  }
}
