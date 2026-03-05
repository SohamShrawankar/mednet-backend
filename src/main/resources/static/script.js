let patientData = [];

document.addEventListener("DOMContentLoaded", () => {
    loadPrefixes();
    loadPatients();
});

function showTab(tabId, element) {
    const tabs = document.querySelectorAll(".tab");
    tabs.forEach(tab => tab.classList.remove("active"));

    const navItems = document.querySelectorAll(".nav-item");
    navItems.forEach(item => item.classList.remove("active"));

    const targetTab = document.getElementById(tabId);
    if (targetTab) {
        targetTab.classList.add("active");
    }

    if (element) {
        element.classList.add("active");
    }
}

function openModal() {
    const modal = document.getElementById("modal");
    const idField = document.getElementById("patientId");
    const regField = document.getElementById("patientReg");
    if (idField && !idField.value) {
        idField.value = `PID-${Math.floor(Math.random() * 90000 + 10000)}`;
    }
    if (regField && !regField.value) {
        const today = new Date();
        regField.value = today.toISOString().slice(0, 10);
    }
    if (modal) modal.style.display = "flex";
}

function closeModal() {
    const modal = document.getElementById("modal");
    const idField = document.getElementById("patientId");
    const regField = document.getElementById("patientReg");
    const status = document.getElementById("autosaveStatus");
    const nameInput = document.getElementById("patientName");
    const ageInput = document.getElementById("patientAge");
    const editor = document.getElementById("editor");
    if (idField) idField.value = "";
    if (regField) regField.value = "";
    if (nameInput) nameInput.value = "";
    if (ageInput) ageInput.value = "";
    if (editor) editor.innerHTML = "";
    if (status) status.textContent = "Auto-save: Draft not saved";
    if (modal) modal.style.display = "none";
}

window.onclick = function (event) {
    const modal = document.getElementById("modal");
    if (event.target === modal) {
        closeModal();
    }
};

function format(command) {
    document.execCommand(command, false, null);
}

function saveDraft() {
    const notesNode = document.getElementById("editor");
    const notes = notesNode ? notesNode.innerHTML : "";
    const status = document.getElementById("autosaveStatus");
    if (!notes || notes === "<br>") {
        alert("Editor is empty.");
        return;
    }
    if (status) {
        const now = new Date();
        const time = now.toLocaleTimeString([], {hour: "2-digit", minute: "2-digit"});
        status.textContent = `Auto-save: Draft saved ${time}`;
    }
    alert("Draft saved locally (Session).");
}

async function savePatient() {
    const nameInput = document.getElementById("patientName");
    const statusInput = document.getElementById("patientStatus");
    const genderInput = document.getElementById("patientGender");
    const ageInput = document.getElementById("patientAge");
    const regInput = document.getElementById("patientReg");
    const idInput = document.getElementById("patientId");
    const editor = document.getElementById("editor");

    const name = nameInput ? nameInput.value.trim() : "";
    const status = statusInput ? statusInput.value : "Active";
    const gender = genderInput ? genderInput.value : "Other";
    const age = ageInput && ageInput.value ? Number(ageInput.value) : null;
    const registeredDate = regInput && regInput.value ? regInput.value : null;
    const patientCode = idInput && idInput.value ? idInput.value : null;
    const notes = editor ? editor.innerHTML : "";

    if (!name) {
        alert("Patient name is required.");
        return;
    }

    const payload = {
        patientCode,
        name,
        age,
        gender,
        status,
        registeredDate,
        notes
    };

    try {
        const response = await fetch("/api/patients", {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify(payload)
        });

        if (!response.ok) {
            throw new Error("Unable to save patient");
        }

        closeModal();
        await loadPatients();
    } catch (error) {
        console.error("Save failed:", error);
        alert("Unable to save patient.");
    }
}

function renderTable(data = patientData) {
    const tbody = document.querySelector("#patientTable tbody");
    if (!tbody) return;

    tbody.innerHTML = "";

    data.forEach(p => {
        const row = `
            <tr>
                <td><strong>${p.name || "-"}</strong></td>
                <td>${p.age ?? "-"}</td>
                <td>${p.gender || "-"}</td>
                <td><span class="status-badge">${p.status || "-"}</span></td>
                <td>${p.registeredDate || "-"}</td>
                <td>
                    <button class="btn-secondary" onclick="deletePatient(${p.id})">Delete</button>
                </td>
            </tr>
        `;
        tbody.innerHTML += row;
    });
}

async function loadPatients() {
    try {
        const response = await fetch("/api/patients");
        if (!response.ok) {
            throw new Error("Unable to fetch patients");
        }
        patientData = await response.json();
        renderTable();
    } catch (error) {
        console.warn("Patient API not reachable.");
        patientData = [];
        renderTable();
    }
}

async function deletePatient(id) {
    try {
        const response = await fetch(`/api/patients/${id}`, {method: "DELETE"});
        if (!response.ok) {
            throw new Error("Unable to delete patient");
        }
        await loadPatients();
    } catch (error) {
        console.error("Delete failed:", error);
    }
}

function searchTable() {
    const inputEl = document.getElementById("searchInput");
    const input = inputEl ? inputEl.value.toLowerCase() : "";
    const filtered = patientData.filter(p => (p.name || "").toLowerCase().includes(input));
    renderTable(filtered);
}

async function savePrefix() {
    const name = document.getElementById("prefixName").value;
    const gender = document.getElementById("prefixGender").value;
    const prefixOf = document.getElementById("prefixOf").value;

    if (!name || !prefixOf) {
        alert("Please fill all prefix fields.");
        return;
    }

    const payload = {prefixName: name, gender: gender, prefixOf: prefixOf};

    try {
        const response = await fetch("/api/prefix", {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify(payload)
        });

        if (response.ok) {
            document.getElementById("prefixName").value = "";
            document.getElementById("prefixOf").value = "";
            loadPrefixes();
        }
    } catch (error) {
        console.error("API Error:", error);
    }
}

async function loadPrefixes() {
    try {
        const response = await fetch("/api/prefix");
        const data = await response.json();

        const tbody = document.querySelector("#prefixTable tbody");
        if (!tbody) return;

        tbody.innerHTML = "";
        data.forEach(p => {
            tbody.innerHTML += `
                <tr>
                    <td>${p.id}</td>
                    <td>${p.prefixName}</td>
                    <td>${p.gender}</td>
                    <td>${p.prefixOf}</td>
                    <td>
                        <button class="btn-secondary" onclick="deletePrefix(${p.id})">Delete</button>
                    </td>
                </tr>
            `;
        });
    } catch (error) {
        console.warn("Backend not reachable. Ensure Spring Boot is running.");
    }
}

async function deletePrefix(id) {
    try {
        await fetch(`/api/prefix/${id}`, {method: "DELETE"});
        loadPrefixes();
    } catch (error) {
        console.error("Delete failed:", error);
    }
}

function downloadExcel() {
    window.location.href = "/api/prefix/download";
}

async function uploadExcel() {
    const fileInput = document.getElementById("excelFile");
    if (!fileInput.files.length) {
        alert("Please select an Excel file first.");
        return;
    }

    const formData = new FormData();
    formData.append("file", fileInput.files[0]);

    try {
        const response = await fetch("/api/prefix/upload", {
            method: "POST",
            body: formData
        });
        if (response.ok) {
            alert("Upload successful.");
            loadPrefixes();
        }
    } catch (error) {
        alert("Upload failed. Check server logs.");
    }
}

function downloadPDF() {
    window.location.href = "/api/prefix/pdf";
}

async function loadPrefixesWS() {
    const list = document.getElementById("wsPrefixList");
    if (!list) return;

    list.innerHTML = "<li>Loading data from web service...</li>";

    try {
        const response = await fetch("/api/prefix");
        const data = await response.json();

        list.innerHTML = "";
        data.forEach(p => {
            list.innerHTML += `<li>
                <span><strong>${p.prefixName}</strong> (${p.gender})</span>
                <small>ID: ${p.id}</small>
            </li>`;
        });
    } catch (error) {
        list.innerHTML = "<li style='color:red'>Failed to fetch service data.</li>";
    }
}
