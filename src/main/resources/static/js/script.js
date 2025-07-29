// Function to create and display the iframe
function loadInFrame(url) {
  // Check if the iframe container already exists, if not create it
  let iframeContainer = document.getElementById('iframeContainer');

  // If the container doesn't exist, create it dynamically
  if (!iframeContainer) {
    // Create overlay background
    const overlay = document.createElement('div');
    overlay.id = 'iframeOverlay';
    overlay.style.position = 'fixed';
    overlay.style.top = '0';
    overlay.style.left = '0';
    overlay.style.width = '100%';
    overlay.style.height = '100%';
    overlay.style.backgroundColor = 'rgba(0, 0, 0, 0.7)';
    overlay.style.zIndex = '999';
    overlay.style.transition = 'opacity 0.3s ease';
    document.body.appendChild(overlay);

    // Create the iframe container
    iframeContainer = document.createElement('div');
    iframeContainer.id = 'iframeContainer';
    iframeContainer.style.position = 'fixed';
    iframeContainer.style.top = '50%';
    iframeContainer.style.left = '50%';
    iframeContainer.style.transform = 'translate(-50%, -50%)';
    iframeContainer.style.width = '70%';
    iframeContainer.style.maxWidth = '600px';
    iframeContainer.style.height = '400px';
    iframeContainer.style.backgroundColor = '#fff';
    iframeContainer.style.borderRadius = '16px';
    iframeContainer.style.padding = '20px';
    iframeContainer.style.boxShadow = '0 10px 30px rgba(0, 0, 0, 0.3)';
    iframeContainer.style.overflow = 'hidden';
    iframeContainer.style.zIndex = '1000';
    iframeContainer.style.transition = 'all 0.3s ease';
    iframeContainer.style.opacity = '0'; // Initial state for transition
    iframeContainer.style.display = 'flex';
    iframeContainer.style.flexDirection = 'column';

    // Create the header for styling
    const header = document.createElement('div');
    header.id = 'iframeHeader';
    header.style.backgroundColor = '#00b2ff';
    header.style.color = '#fff';
    header.style.padding = '12px 20px';
    header.style.borderTopLeftRadius = '16px';
    header.style.borderTopRightRadius = '16px';
    header.style.marginBottom = '5px';
    header.style.display = 'flex';
    header.style.justifyContent = 'space-between';
    header.style.alignItems = 'center';

    // Create and configure the title
    const title = document.createElement('span');
    title.textContent = 'Member Details';
    title.style.fontSize = '18px';
    title.style.fontWeight = 'bold';
    title.style.flexGrow = '1';
    title.style.textAlign = 'left';
    header.appendChild(title);

    // Create and configure the close button
    const closeButton = document.createElement('button');
    closeButton.textContent = '✕';
    closeButton.style.backgroundColor = '#d9534f';
    closeButton.style.color = '#fff';
    closeButton.style.border = 'none';
    closeButton.style.padding = '8px 12px';
    closeButton.style.cursor = 'pointer';
    closeButton.style.borderRadius = '50%';
    closeButton.style.fontSize = '18px';
    closeButton.style.transition = 'background-color 0.3s ease';
    closeButton.style.marginLeft = '20px';

    // Add hover effect to the close button
    closeButton.onmouseover = function () {
      closeButton.style.backgroundColor = '#c9302c';
    };
    closeButton.onmouseout = function () {
      closeButton.style.backgroundColor = '#d9534f';
    };

    // Add click event to the close button to remove the iframe container and overlay
    closeButton.onclick = function () {
      iframeContainer.style.opacity = '0';
      overlay.style.opacity = '0';
      setTimeout(() => {
        document.getElementById('iframeContainer').remove();
        document.getElementById('iframeOverlay').remove();
      }, 300);
    };

    // Append close button to header
    header.appendChild(closeButton);

    // Create and configure the iframe
    const iframe = document.createElement('iframe');
    iframe.id = 'popupFrame';
    iframe.width = '100%';
    iframe.height = '100%';
    iframe.style.border = 'none';
    iframe.src = url; // Set the URL of the iframe to the clicked link

    // Append the header and the iframe to the container
    iframeContainer.appendChild(header);
    iframeContainer.appendChild(iframe);

    // Append the container to the body
    document.body.appendChild(iframeContainer);

    // Set opacity for smooth transition effect
    setTimeout(() => {
      iframeContainer.style.opacity = '1';
      overlay.style.opacity = '1';
    }, 10);
  } else {
    // If the container already exists, just change the URL of the iframe
    document.getElementById('popupFrame').src = url;
  }

  return false;
}

// Validation functions for table editing
function validateTableName(name) {
  if (!name || name.trim() === '') {
    return 'Name is required.';
  }
  if (/\d/.test(name)) {
    return 'Name must not contain numbers.';
  }
  if (name.length > 25) {
    return 'Name must be 25 characters or less.';
  }
  return null;
}

function validateTableEmail(email) {
  if (!email || email.trim() === '') {
    return 'Email is required.';
  }
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!emailRegex.test(email)) {
    return 'Please enter a valid email address.';
  }
  return null;
}

function validateTablePhone(phone) {
  if (!phone || phone.trim() === '') {
    return 'Phone number is required.';
  }
  const phoneRegex = /^\d{10,12}$/;
  if (!phoneRegex.test(phone)) {
    return 'Phone number must be 10-12 digits.';
  }
  return null;
}

function validateTableRole(role) {
  if (!role || role.trim() === '') {
    return 'Role is required.';
  }
  if (role !== 'ADMIN' && role !== 'USER') {
    return 'Role must be either ADMIN or USER.';
  }
  return null;
}

// Real-time validation for table fields
function validateTableField(field, fieldType) {
  const value = field.value.trim();
  let error = null;
  
  switch(fieldType) {
    case 'name':
      error = validateTableName(value);
      break;
    case 'email':
      error = validateTableEmail(value);
      break;
    case 'phone':
      error = validateTablePhone(value);
      break;
    case 'role':
      error = validateTableRole(value);
      break;
  }
  
  // Remove existing error styling
  field.classList.remove('error-field');
  
  // Remove existing error message
  const existingError = field.parentNode.querySelector('.field-error');
  if (existingError) {
    existingError.remove();
  }
  
  // Add error styling and message if there's an error
  if (error) {
    field.classList.add('error-field');
    const errorDiv = document.createElement('div');
    errorDiv.className = 'field-error';
    errorDiv.style.color = 'red';
    errorDiv.style.fontSize = '12px';
    errorDiv.style.marginTop = '2px';
    errorDiv.textContent = error;
    field.parentNode.appendChild(errorDiv);
  }
}

function editMember(button) {
  const row = button.closest('tr');
  const fields = row.querySelectorAll('.editable-field');
  fields.forEach(field => {
    field.disabled = false;
    field.classList.add('edit-mode');
  });

  // Show Save and Cancel links, hide Edit button
  const saveLink = row.querySelector('.save-link');
  const cancelButton = row.querySelector('.cancel-button');
  saveLink.style.display = 'inline';
  cancelButton.style.display = 'inline';
  button.style.display = 'none';

  // Store original values in data attributes to restore on cancel
  row.setAttribute('data-original-name', fields[0].value);
  row.setAttribute('data-original-email', fields[1].value);
  row.setAttribute('data-original-phone', fields[2].value);
  row.setAttribute('data-original-role', fields[3].value);
}

// Function to cancel edit mode and restore original values
function cancelEdit(button) {
  const row = button.closest('tr');
  const fields = row.querySelectorAll('.editable-field');

  // Restore original values
  fields[0].value = row.getAttribute('data-original-name');
  fields[1].value = row.getAttribute('data-original-email');
  fields[2].value = row.getAttribute('data-original-phone');
  fields[3].value = row.getAttribute('data-original-role');

  // Disable fields and remove edit-mode styling
  fields.forEach(field => {
    field.disabled = true;
    field.classList.remove('edit-mode');
  });

  // Hide Save and Cancel links, show Edit button again
  const saveLink = row.querySelector('.save-link');
  const editButton = row.querySelector('.edit-button');
  saveLink.style.display = 'none';
  button.style.display = 'none';
  editButton.style.display = 'inline';
}

// Function to delete a member
function deleteMember(anchor, url) {
  if (confirm("Are you sure you want to delete this member?")) {
    fetch(url, {
      method: 'DELETE'
    })
    .then(response => {
      if (response.ok) {
        alert("Member deleted successfully!");
        anchor.closest('tr').remove(); // Remove the table row
      } else {
        alert("Failed to delete member.");
      }
    })
    .catch(error => console.error('Error deleting member:', error));
  }
}

// Function to save updated member information
function saveMember(anchor, url) {
  const row = anchor.closest('tr');
  const fields = row.querySelectorAll('.editable-field');

  // Validate all fields before sending update
  const name = fields[0].value.trim();
  const email = fields[1].value.trim();
  const phone = fields[2].value.trim();
  const role = fields[3].value;

  // Perform validation
  const nameError = validateTableName(name);
  const emailError = validateTableEmail(email);
  const phoneError = validateTablePhone(phone);
  const roleError = validateTableRole(role);

  // If there are validation errors, show them and don't proceed
  if (nameError || emailError || phoneError || roleError) {
    let errorMessage = 'Please fix the following errors:\n';
    if (nameError) errorMessage += `• ${nameError}\n`;
    if (emailError) errorMessage += `• ${emailError}\n`;
    if (phoneError) errorMessage += `• ${phoneError}\n`;
    if (roleError) errorMessage += `• ${roleError}\n`;
    
    alert(errorMessage);
    return;
  }

  // Get updated field values (excluding password - password remains unchanged)
  const updatedMember = {
    name: name,
    email: email,
    phoneNumber: phone,
    role: role
    // Password is not included - it will remain unchanged
  };

  // Send the updated member data to the server using the URL
  fetch(url, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(updatedMember)
  })
  .then(response => {
    if (response.ok) {
      alert("Member updated successfully!");

      // Disable fields again after saving
      fields.forEach(field => {
        field.disabled = true;
        field.classList.remove('edit-mode');
      });

      // Show Edit button and hide Save/Cancel links
      const editButton = row.querySelector('.edit-button');
      const cancelButton = row.querySelector('.cancel-button');
      anchor.style.display = 'none';
      cancelButton.style.display = 'none';
      editButton.style.display = 'inline';
    } else {
      // Extract error message from response and display it in the alert
      response.json().then(errorMessage => {
        alert(`Failed to update member. Error: ${errorMessage.message || "Unknown error"}`);
      });
    }
  })
  .catch(error => {
    console.error('Error updating member:', error);
    alert(`Failed to update member. Error: ${error.message}`);
  });
}




