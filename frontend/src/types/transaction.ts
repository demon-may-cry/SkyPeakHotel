export interface Transaction {

    id: string;

    amount: number;

    type: "DEPOSIT" | "WITHDRAW";

    description: string;

    createdAt: string;

}