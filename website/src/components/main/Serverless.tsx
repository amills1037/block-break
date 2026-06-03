import ServerlessButton from "@/components/main/ServerlessButton";
import H2 from "@ui/H2";
import HeroBox from "@ui/HeroBox";
import P from "@ui/P";

function Serverless() {
    return (
        <HeroBox>
            <H2>Serverless</H2>
            <P>
                <div className="h-40">
                    Choose this version to see the Terraform version in action.
                    The serverless version uses AWS Lambda and AWS DynamoDB.
                </div>
            </P>
            <ServerlessButton />
        </HeroBox>
    );
}

export default Serverless;
