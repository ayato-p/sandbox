import { LitElement, html } from "lit";
import { customElement, property } from "lit/decorators.js";
import { repeat } from "lit/directives/repeat.js";

type Company = {
  id: String;
  companyName: String;
};

@customElement("parallel-component")
export class ParallelComponent extends LitElement {
  @property() companies: { id: String; companyName: String }[] = [];

  constructor() {
    super();
  }

  fetchIds = (): Promise<String[]> =>
    fetch("/api/companyIds").then((x) => x.json());

  fetchCompany = (id: String): Promise<Company> =>
    fetch("/api/companies/" + id).then((x) => x.json());

  render() {
    return html`
      <div>
        <h2>Parallel: ${this.companies.length}</h2>
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

    this.fetchIds().then((ids: String[]) => {
      ids.forEach((id) => {
        this.fetchCompany(id).then(
          (company) =>
            (this.companies = [...this.companies, company].sort((a, b) =>
              a.companyName > b.companyName ? 1 : -1
            ))
        );
      });
    });
  }
}
