import { LitElement, html } from "lit";
import { customElement, property } from "lit/decorators.js";
import { repeat } from "lit/directives/repeat.js";

import oboe from "oboe";

type Company = {
  id: String;
  companyName: String;
};

class Observer {
  private fns: any[] = [];

  addListener = (f: any): void => {
    this.fns.push(f);
  };
  notify = (company: Company): void => {
    this.fns.forEach((f) => f(company));
  };
}

const observer = new Observer();

@customElement("stream-component")
export class StreamComponent extends LitElement {
  @property() companies: {
    id: String;
    companyName: String;
  }[] = [];

  constructor() {
    super();
    observer.addListener((company: Company) => {
      this.companies = [...this.companies, company].sort((a, b) =>
        a.companyName > b.companyName ? 1 : -1
      );
    });
  }

  render() {
    return html`
      <div>
        <h2>Stream: ${this.companies.length}</h2>
        <ul>
          ${repeat(
            this.companies,
            (company) => company.id,
            (company, _) => html` <li>${company.companyName}</li> `
          )}
        </ul>
      </div>
    `;
  }

  async connectedCallback() {
    super.connectedCallback();
    oboe("/api/companies?limit=1000").node("!.*", (company: Company) => {
      observer.notify(company);
    });
  }
}
