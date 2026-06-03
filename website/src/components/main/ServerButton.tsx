import Button from "@ui/Button";
import A from "@ui/A";

interface ButtonProps {
    db?: "random" | "mysql" | "postgresql" | "mongodb";
}

function ServerButton({ db = "random" }: ButtonProps) {
    switch (db) {
        case "random":
            return <Button href="/server">Server</Button>;
        case "mysql":
            return <A href="/server?db=mysql">MySQL</A>;
        case "postgresql":
            return <A href="/server?db=postgresql">PostgreSQL</A>;
        case "mongodb":
            return <A href="/server?db=mongodb">MongoDB</A>;
    }
}

export default ServerButton;
