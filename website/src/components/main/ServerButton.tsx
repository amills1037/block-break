import Button from "@ui/Button";
import A from "@ui/A";

interface ButtonProps {
    db?: "random" | "mysql" | "postgresql" | "mongodb";
}

function ServerButton({ db = "random" }: ButtonProps) {
    switch (db) {
        case "random":
            return <Button href="/client?db=0">Server</Button>;
        case "mysql":
            return <A href="/client?db=1">MySQL</A>;
        case "postgresql":
            return <A href="/client?db=2">PostgreSQL</A>;
        case "mongodb":
            return <A href="/client?db=3">MongoDB</A>;
    }
}

export default ServerButton;
