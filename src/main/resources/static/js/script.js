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
  // Phone number is optional, so if empty, it's valid
  if (!phone || phone.trim() === '') {
    return null; // No error for empty phone number
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
  
  console.log('Edit button clicked, found fields:', fields.length);
  
  fields.forEach((field, index) => {
    console.log(`Field ${index}:`, field);
    field.disabled = false;
    field.classList.add('edit-mode');
  });

  // Show Save and Cancel links, hide Edit button
  const saveLink = row.querySelector('.save-link');
  const cancelButton = row.querySelector('.cancel-button');
  const editButton = row.querySelector('.edit-button');
  
  if (saveLink) saveLink.style.display = 'inline';
  if (cancelButton) cancelButton.style.display = 'inline';
  if (editButton) editButton.style.display = 'none';

  // Store original values in data attributes to restore on cancel
  if (fields.length >= 4) {
    row.setAttribute('data-original-name', fields[0].value);
    row.setAttribute('data-original-email', fields[1].value);
    row.setAttribute('data-original-phone', fields[2].value);
    row.setAttribute('data-original-role', fields[3].value);
  }
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

// Modal functions
function showModal(title, message, type = 'info') {
  // Remove existing modal if any
  const existingModal = document.getElementById('customModal');
  if (existingModal) {
    existingModal.remove();
  }

  const modal = document.createElement('div');
  modal.id = 'customModal';
  modal.style.position = 'fixed';
  modal.style.top = '0';
  modal.style.left = '0';
  modal.style.width = '100%';
  modal.style.height = '100%';
  modal.style.backgroundColor = 'rgba(0, 0, 0, 0.7)';
  modal.style.zIndex = '10000';
  modal.style.display = 'flex';
  modal.style.alignItems = 'center';
  modal.style.justifyContent = 'center';

  const modalContent = document.createElement('div');
  modalContent.style.backgroundColor = 'var(--bg-primary)';
  modalContent.style.borderRadius = '12px';
  modalContent.style.padding = '30px';
  modalContent.style.maxWidth = '800px';
  modalContent.style.width = '90%';
  modalContent.style.maxHeight = '80vh';
  modalContent.style.overflowY = 'auto';
  modalContent.style.boxShadow = '0 10px 30px rgba(0, 0, 0, 0.3)';
  modalContent.style.position = 'relative';

  // Set color based on type
  let color = 'var(--primary-color)';
  if (type === 'success') color = 'var(--success-color)';
  if (type === 'error') color = 'var(--error-color)';
  if (type === 'warning') color = 'var(--warning-color)';

  modalContent.innerHTML = `
    <h3 style="margin: 0 0 20px 0; color: ${color}; font-size: 20px;">${title}</h3>
    <p style="margin: 0 0 25px 0; color: var(--text-primary); line-height: 1.5;">${message}</p>
    <button onclick="closeModal()" style="
      background: ${color};
      color: white;
      border: none;
      padding: 12px 24px;
      border-radius: 6px;
      cursor: pointer;
      font-size: 14px;
      font-weight: 500;
      transition: all 0.3s ease;
    ">OK</button>
  `;

  modal.appendChild(modalContent);
  document.body.appendChild(modal);

  // Close modal on background click
  modal.addEventListener('click', function(e) {
    if (e.target === modal) {
      closeModal();
    }
  });
}

function closeModal() {
  const modal = document.getElementById('customModal');
  if (modal) {
    modal.remove();
  }
}

// Function to delete a member
function deleteMember(anchor, url) {
  // Remove existing modal if any
  const existingModal = document.getElementById('customModal');
  if (existingModal) {
    existingModal.remove();
  }

  const modal = document.createElement('div');
  modal.id = 'customModal';
  modal.style.position = 'fixed';
  modal.style.top = '0';
  modal.style.left = '0';
  modal.style.width = '100%';
  modal.style.height = '100%';
  modal.style.backgroundColor = 'rgba(0, 0, 0, 0.7)';
  modal.style.zIndex = '10000';
  modal.style.display = 'flex';
  modal.style.alignItems = 'center';
  modal.style.justifyContent = 'center';

  const modalContent = document.createElement('div');
  modalContent.style.backgroundColor = 'var(--bg-primary)';
  modalContent.style.borderRadius = '12px';
  modalContent.style.padding = '30px';
  modalContent.style.maxWidth = '400px';
  modalContent.style.width = '90%';
  modalContent.style.boxShadow = '0 10px 30px rgba(0, 0, 0, 0.3)';
  modalContent.style.position = 'relative';

  modalContent.innerHTML = `
    <h3 style="margin: 0 0 20px 0; color: var(--warning-color); font-size: 20px;">Confirm Delete</h3>
    <p style="margin: 0 0 25px 0; color: var(--text-primary); line-height: 1.5;">Are you sure you want to delete this member?</p>
    <div style="display: flex; gap: 10px; justify-content: flex-end;">
      <button onclick="closeModal()" style="
        background: var(--bg-secondary);
        color: var(--text-primary);
        border: 1px solid var(--border-color);
        padding: 10px 20px;
        border-radius: 6px;
        cursor: pointer;
        font-size: 14px;
        font-weight: 500;
        transition: all 0.3s ease;
      ">Cancel</button>
      <button onclick="confirmDelete()" style="
        background: var(--error-color);
        color: white;
        border: none;
        padding: 10px 20px;
        border-radius: 6px;
        cursor: pointer;
        font-size: 14px;
        font-weight: 500;
        transition: all 0.3s ease;
      ">Delete</button>
    </div>
  `;

  modal.appendChild(modalContent);
  document.body.appendChild(modal);

  // Store the anchor and url for the confirmation
  window.pendingDelete = { anchor, url };

  // Close modal on background click
  modal.addEventListener('click', function(e) {
    if (e.target === modal) {
      closeModal();
    }
  });
}

function confirmDelete() {
  const { anchor, url } = window.pendingDelete;
  
  fetch(url, {
    method: 'DELETE',
    headers: getAuthHeaders()
  })
  .then(response => {
    if (response.ok) {
      showModal('Success', 'Member deleted successfully!', 'success');
      anchor.closest('tr').remove(); // Remove the table row
    } else if (response.status === 403) {
      response.text().then(errorMessage => {
        showModal('Access Denied', 'Cannot delete your own account. Please contact an administrator if you need to delete your account.', 'error');
      });
    } else {
      response.text().then(errorMessage => {
        showModal('Error', `Failed to delete member. Status: ${response.status}`, 'error');
      });
    }
  })
  .catch(error => {
    console.error('Delete error:', error);
    showModal('Error', 'Failed to delete member. Please try again.', 'error');
  });
  
  closeModal();
}

// Function to load members in a modal
function loadMembersModal() {
  console.log('Loading members modal...');
  const headers = getAuthHeaders();
  console.log('Request headers:', headers);
  
  fetch('/admin/members', {
    headers: headers
  })
    .then(response => {
      console.log('Members response status:', response.status);
      console.log('Members response headers:', response.headers);
      if (!response.ok) {
        return response.text().then(errorText => {
          throw new Error(`HTTP ${response.status}: ${errorText}`);
        });
      }
      return response.json();
    })
    .then(data => {
      console.log('Members data received:', data);
      let membersHtml = '';
      if (data && data.length > 0) {
        membersHtml = '<table style="width: 100%; border-collapse: collapse; margin-top: 15px;">';
        membersHtml += '<thead><tr style="background: var(--bg-secondary);">';
        membersHtml += '<th style="padding: 10px; text-align: left; border-bottom: 1px solid var(--border-color);">Name</th>';
        membersHtml += '<th style="padding: 10px; text-align: left; border-bottom: 1px solid var(--border-color);">Email</th>';
        membersHtml += '<th style="padding: 10px; text-align: left; border-bottom: 1px solid var(--border-color);">Phone Number</th>';
        membersHtml += '<th style="padding: 10px; text-align: left; border-bottom: 1px solid var(--border-color);">Role</th>';
        membersHtml += '</tr></thead><tbody>';
        
        data.forEach(member => {
          membersHtml += '<tr style="border-bottom: 1px solid var(--border-color);">';
          membersHtml += `<td style="padding: 10px;">${member.name || 'N/A'}</td>`;
          membersHtml += `<td style="padding: 10px;">${member.email || 'N/A'}</td>`;
          membersHtml += `<td style="padding: 10px;">${member.phoneNumber || 'N/A'}</td>`;
          membersHtml += `<td style="padding: 10px;">${member.role || 'N/A'}</td>`;
          membersHtml += '</tr>';
        });
        
        membersHtml += '</tbody></table>';
      } else {
        membersHtml = '<p style="text-align: center; color: var(--text-secondary);">No members found.</p>';
      }
      
      showModal('Members List', membersHtml, 'info');
    })
    .catch(error => {
      console.error('Error loading members:', error);
      showModal('Error', 'Failed to load members data: ' + error.message, 'error');
    });
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
    
    showModal('Validation Error', errorMessage, 'error');
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
    headers: getAuthHeaders(),
    body: JSON.stringify(updatedMember)
  })
  .then(response => {
    if (response.ok) {
      showModal('Success', 'Member updated successfully!', 'success');

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
    } else if (response.status === 403) {
      response.text().then(errorMessage => {
        showModal('Access Denied', 'Cannot edit your own account. Please contact an administrator if you need to modify your account.', 'error');
      });
      
      // Cancel edit mode and restore original values
      cancelEdit(row.querySelector('.cancel-button'));
    } else {
      // Extract error message from response and display it in the modal
      response.text().then(errorMessage => {
        showModal('Error', `Failed to update member. Error: ${errorMessage}`, 'error');
      });
    }
  })
  .catch(error => {
    console.error('Error updating member:', error);
    showModal('Error', `Failed to update member. Error: ${error.message}`, 'error');
  });
}

// Utility function to get JWT token from localStorage
function getAuthToken() {
  const token = localStorage.getItem('accessToken');
  console.log('Getting auth token from localStorage:', token ? 'Token found' : 'No token found');
  if (!token) {
    console.log('localStorage contents:', {
      accessToken: localStorage.getItem('accessToken'),
      refreshToken: localStorage.getItem('refreshToken'),
      userEmail: localStorage.getItem('userEmail'),
      userRole: localStorage.getItem('userRole')
    });
  }
  return token;
}

// Utility function to add Authorization header to fetch requests
function getAuthHeaders() {
  const token = getAuthToken();
  const headers = {
    'Content-Type': 'application/json',
    ...(token && { 'Authorization': `Bearer ${token}` })
  };
  console.log('Auth headers being sent:', headers);
  console.log('Token present:', !!token);
  return headers;
}




