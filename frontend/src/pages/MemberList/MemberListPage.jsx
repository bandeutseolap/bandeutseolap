import React, {useEffect, useState} from 'react';
import axios from "axios";

const MemberListPage = () => {
   const [members, setMembers] = useState([]);

    useEffect(() => {
        const fetchData = async () => {
            await axios.get("/members")
                .then((res) => {
                    setMembers(res.data);
                    console.log(res);

                })
                .catch((err) => {
                    console.error(err);
                })
        }

        fetchData();
    },[]);

    return (
        <div>
            <table>
                <thead>
                <tr>
                    <th>#</th>
                    <th>이름</th>
                </tr>
                </thead>
                <tbody>
                {members.map((member, index) => (
                    <tr key={member.id}>
                        <td>{member.id}</td>
                        <td>{member.name}</td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
};

export default MemberListPage;